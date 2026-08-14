package com.jikku.backend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/** 카카오 로그인 요청. 프론트가 카카오 SDK로 이미 받아둔 액세스 토큰을 그대로 보낸다. */
@Builder
public record KakaoLoginRequest(

        @NotBlank(message = "accessToken은 필수입니다.")
        String accessToken
) {
}
