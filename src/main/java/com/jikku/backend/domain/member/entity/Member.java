package com.jikku.backend.domain.member.entity;

import com.jikku.backend.domain.member.enums.SocialLogin;
import com.jikku.backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
// social_uid는 소셜 종류(KAKAO 등) 안에서 유일 → (social_login, social_uid) 복합 유니크로 upsert 기준 보장 (CLAUDE.md §5.1)
@Table(name = "member", uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_social", columnNames = {"social_login", "social_uid"})
})
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialLogin socialLogin;

    @Column(nullable = false)
    private String socialUid;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;
}
