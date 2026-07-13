package com.jikku.backend.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 자체 JWT(Access Token)의 발급·검증을 담당하는 엔진.
 * 로그인 방식(지금은 dev-login, 이후 카카오)과 무관하게 공통으로 쓰므로 도메인이 아닌 global/security에 둔다. (CLAUDE.md §5.1)
 * HS256 대칭키 서명 방식.
 */
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessExpiration;

    // secret/만료는 application.yml(→ 환경변수)에서 주입. 하드코딩 금지 (CLAUDE.md §1-4)
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpiration) {
        // HS256은 최소 256bit(32바이트) 키가 필요 → secret 문자열의 바이트로 키 생성
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
    }

    /** memberId를 subject에 담아 서명된 Access 토큰을 발급한다. */
    public String createAccessToken(Long memberId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessExpiration);

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /** 서명·만료를 검증한다. 유효하면 true, 위조/만료/형식오류면 false. */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 위조·만료·형식오류 등은 모두 "유효하지 않은 토큰"으로 취급
            return false;
        }
    }

    /** 토큰에서 memberId(subject)를 꺼낸다. (validateToken 통과 후 호출 전제) */
    public Long getMemberId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    // 서명 검증 + 파싱을 한 곳에서 처리 (검증 실패 시 예외 발생)
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
