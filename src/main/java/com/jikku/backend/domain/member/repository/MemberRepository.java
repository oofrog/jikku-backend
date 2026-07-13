package com.jikku.backend.domain.member.repository;

import com.jikku.backend.domain.member.entity.Member;
import com.jikku.backend.domain.member.enums.SocialLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findBySocialLoginAndSocialUid(SocialLogin socialLogin, String socialUid);
}
