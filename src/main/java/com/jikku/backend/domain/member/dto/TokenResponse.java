package com.jikku.backend.domain.member.dto;

/**
 * 로그인 성공 시 내려주는 토큰 응답 DTO.
 * grantType은 클라이언트가 헤더에 붙일 방식("Bearer")을 알려준다.
 */
public record TokenResponse(String grantType, String accessToken) {

    public static TokenResponse bearer(String accessToken) {
        return new TokenResponse("Bearer", accessToken);
    }
}
