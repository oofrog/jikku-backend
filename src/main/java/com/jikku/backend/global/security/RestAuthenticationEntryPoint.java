package com.jikku.backend.global.security;

import tools.jackson.databind.ObjectMapper;
import com.jikku.backend.global.apiPayload.ApiResponse;
import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 인증되지 않은 요청(토큰 없음/유효하지 않음)이 보호된 자원에 접근할 때 401 응답을 만든다.
 * 스프링 기본 응답 대신 앱 공통 포맷(ApiResponse)으로 통일하기 위해 직접 구현한다. (CLAUDE.md §3)
 */
@Component
@RequiredArgsConstructor
@NullMarked
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        writeErrorResponse(response);
    }

    private void writeErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(GeneralErrorCode.UNAUTHORIZED.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(),
                ApiResponse.onFailure(GeneralErrorCode.UNAUTHORIZED));
    }
}
