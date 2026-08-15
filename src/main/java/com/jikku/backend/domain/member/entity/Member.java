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
// social_uid는 소셜 종류 안에서만 유일하므로 (social_login, social_uid) 복합 유니크로 upsert 기준을 보장
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

    /**
     * 발급된 Refresh 토큰의 SHA-256 해시. 원문을 저장하면 DB 유출이 그대로 계정 탈취가 된다.
     * null이면 로그인 상태가 아니라는 뜻이라 재발급이 거부된다(= 로그아웃).
     * 회원당 한 칸이므로 다른 기기에서 로그인하면 이전 기기의 Refresh 토큰은 무효가 된다.
     */
    private String refreshToken;

    public void updateRefreshToken(String refreshTokenHash) {
        this.refreshToken = refreshTokenHash;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
    }
}
