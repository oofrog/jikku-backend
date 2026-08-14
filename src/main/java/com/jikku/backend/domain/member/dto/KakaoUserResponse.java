package com.jikku.backend.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 /v2/user/me 응답 중 우리가 쓰는 부분만 담는다. (외부 응답 역직렬화 전용)
 * 이메일·닉네임은 사용자가 동의하지 않으면 키 자체가 빠져 오므로 중첩 객체까지 null일 수 있다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserResponse(
        Long id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {

    /** member.social_uid에 넣을 값. 카카오 회원번호는 숫자지만 소셜 종류가 늘어도 되도록 문자열로 통일한다. */
    public String socialUid() {
        return id == null ? null : String.valueOf(id);
    }

    public String email() {
        return kakaoAccount == null ? null : kakaoAccount.email();
    }

    public String nickname() {
        if (kakaoAccount == null || kakaoAccount.profile() == null) {
            return null;
        }
        return kakaoAccount.profile().nickname();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KakaoAccount(String email, Profile profile) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Profile(String nickname) {
    }
}
