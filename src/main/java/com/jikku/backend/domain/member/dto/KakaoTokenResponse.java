package com.jikku.backend.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 /oauth/token 응답 중 우리가 쓰는 부분만 담는다. (외부 API 응답 역직렬화 전용)
 * 카카오가 주는 refresh_token은 카카오 API용이라 우리 서비스 JWT와 무관하고, 지금은 쓰지 않아 받지 않는다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoTokenResponse(
        @JsonProperty("access_token") String accessToken
) {
}
