package com.jikku.backend.domain.member.repository;

import com.jikku.backend.domain.member.entity.Member;
import com.jikku.backend.domain.member.enums.SocialLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 회원 저장소 (JPA).
 * upsert 기준인 (socialLogin, socialUid)로 회원을 조회한다. (CLAUDE.md §5.1)
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findBySocialLoginAndSocialUid(SocialLogin socialLogin, String socialUid);
}
