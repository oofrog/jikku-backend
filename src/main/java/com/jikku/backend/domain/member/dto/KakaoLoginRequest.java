package com.jikku.backend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * 카카오 로그인 요청. 프론트가 리다이렉트로 받은 인가 코드를 그대로 보낸다.
 *
 * <p>redirectUri를 프론트에서 받는 이유: 인가 코드는 발급 때 쓴 redirect_uri에 묶여 있어 교환 요청에도
 * 같은 값을 보내야 하는데, 로컬·배포에서 값이 달라 서버가 하나로 고정할 수 없다. 임의 주소를 넣어도
 * 카카오 콘솔에 등록된 값이 아니면 카카오가 거부하므로 열어둬도 안전하다.
 */
@Builder
public record KakaoLoginRequest(

        @NotBlank(message = "code는 필수입니다.")
        String code,

        @NotBlank(message = "redirectUri는 필수입니다.")
        String redirectUri
) {
}
