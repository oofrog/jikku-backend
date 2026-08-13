package com.jikku.backend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/** 토큰 재발급 요청. Access 토큰은 이미 만료됐을 것이므로 Refresh 토큰만 받는다. */
@Builder
public record ReissueRequest(

        @NotBlank(message = "refreshToken은 필수입니다.")
        String refreshToken
) {
}
