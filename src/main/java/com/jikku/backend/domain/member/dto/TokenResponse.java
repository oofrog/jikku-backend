package com.jikku.backend.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 성공 시 내려주는 토큰 응답 DTO.
 * grantType은 클라이언트가 헤더에 붙일 방식("Bearer")을 알려준다.
 */
@Getter
@Builder
public class TokenResponse {

    private final String grantType;
    private final String accessToken;

    public static TokenResponse bearer(String accessToken) {
        return TokenResponse.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }
}
