package com.jikku.backend.domain.member.dto;

import lombok.Builder;

/**
 * 로그인·재발급 성공 시 내려주는 토큰 응답 DTO.
 * grantType은 클라이언트가 헤더에 붙일 방식("Bearer")을 알려준다.
 */
@Builder
public record TokenResponse(String grantType, String accessToken, String refreshToken) {

    public static TokenResponse bearer(String accessToken, String refreshToken) {
        return TokenResponse.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
