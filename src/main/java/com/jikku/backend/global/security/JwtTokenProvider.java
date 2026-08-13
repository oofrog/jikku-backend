package com.jikku.backend.global.security;

import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import com.jikku.backend.global.exception.BaseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 자체 JWT 발급·검증. HS256 대칭키 서명.
 * Refresh 토큰은 서버에 저장하지 않는다(무상태). 대신 두 토큰에 용도(typ) 클레임을 넣어
 * 수명이 긴 Refresh 토큰이 Access 토큰 자리에 그대로 쓰이는 것을 막는다 — 같은 키로 서명하므로
 * 용도를 구분하지 않으면 서명 검증만으로는 둘을 가려낼 수 없다.
 */
@Component
public class JwtTokenProvider {

    private static final String CLAIM_TYPE = "typ";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final SecretKey key;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        // HS256은 최소 256bit(32바이트) 키 필요
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String createAccessToken(Long memberId) {
        return createToken(memberId, TYPE_ACCESS, accessExpiration);
    }

    public String createRefreshToken(Long memberId) {
        return createToken(memberId, TYPE_REFRESH, refreshExpiration);
    }

    private String createToken(Long memberId, String type, long expiration) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim(CLAIM_TYPE, type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(key)
                .compact();
    }

    public AccessTokenStatus checkAccessToken(String token) {
        Claims claims;
        try {
            claims = parseClaims(token);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰도 클레임은 읽을 수 있다. Refresh 토큰을 Access 자리에 쓴 경우까지
            // "만료됐으니 재발급하라"고 안내하면 클라이언트가 헛돌기 때문에 용도를 먼저 본다.
            return isAccessToken(e.getClaims()) ? AccessTokenStatus.EXPIRED : AccessTokenStatus.INVALID;
        } catch (JwtException | IllegalArgumentException e) {
            return AccessTokenStatus.INVALID;
        }

        return isAccessToken(claims) ? AccessTokenStatus.VALID : AccessTokenStatus.INVALID;
    }

    private boolean isAccessToken(Claims claims) {
        return TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class));
    }

    // checkAccessToken이 VALID를 준 뒤 호출 전제
    public Long getMemberId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    /**
     * 재발급용. 만료와 그 외 실패를 나눠 던져야 클라이언트가 "재로그인"과 "요청이 잘못됨"을 구분할 수 있다.
     */
    public Long getMemberIdFromRefreshToken(String refreshToken) {
        Claims claims;
        try {
            claims = parseClaims(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new BaseException(GeneralErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BaseException(GeneralErrorCode.INVALID_TOKEN);
        }

        if (!TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class))) {
            throw new BaseException(GeneralErrorCode.INVALID_TOKEN);
        }
        return Long.valueOf(claims.getSubject());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
