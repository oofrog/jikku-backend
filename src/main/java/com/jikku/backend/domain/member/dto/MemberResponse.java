package com.jikku.backend.domain.member.dto;

import com.jikku.backend.domain.member.entity.Member;
import com.jikku.backend.domain.member.enums.SocialLogin;
import lombok.Builder;
import lombok.Getter;

/**
 * 회원 정보 응답 DTO. (엔티티 직접 노출 금지 — CLAUDE.md §3)
 * 내부 식별자인 socialUid는 노출하지 않는다.
 */
@Getter
@Builder
public class MemberResponse {

    private final Long memberId;
    private final SocialLogin socialLogin;
    private final String username;
    private final String email;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .socialLogin(member.getSocialLogin())
                .username(member.getUsername())
                .email(member.getEmail())
                .build();
    }
}
