package com.jikku.backend.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * 요청 헤더의 JWT를 검증해 인증 정보를 SecurityContext에 심는 필터.
 * 한 요청당 한 번만 실행되도록 OncePerRequestFilter 를 상속한다.
 * 토큰이 없거나 유효하지 않아도 여기서 막지 않고, 인가 규칙(authorizeHttpRequests)이 걸러 401로 이어지게 둔다. (CLAUDE.md §5.1)
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        // 토큰이 유효하면 "이 요청은 memberId N번"이라고 SecurityContext에 등록
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long memberId = jwtTokenProvider.getMemberId(token);
            // principal=memberId, 권한은 아직 역할 개념이 없어 빈 목록. (3-인자 생성자라 인증됨 상태로 생성)
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    memberId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    // "Authorization: Bearer xxx" 에서 실제 토큰 문자열만 잘라낸다. 형식이 아니면 null.
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(AUTH_HEADER);
        if (bearer != null && bearer.startsWith(BEARER_PREFIX)) {
            return bearer.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
