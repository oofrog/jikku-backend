package com.jikku.backend.domain.member.dto;

import com.jikku.backend.domain.member.entity.Member;
import com.jikku.backend.domain.member.enums.SocialLogin;
import lombok.Builder;

/**
 * 회원 정보 응답 DTO. 내부 식별자인 socialUid는 노출하지 않는다.
 */
@Builder
public record MemberResponse(
        Long memberId,
        SocialLogin socialLogin,
        String username,
        String email
) {

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .socialLogin(member.getSocialLogin())
                .username(member.getUsername())
                .email(member.getEmail())
                .build();
    }
}
