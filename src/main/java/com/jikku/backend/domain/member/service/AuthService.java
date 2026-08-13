package com.jikku.backend.domain.member.service;

import com.jikku.backend.domain.member.dto.TokenResponse;
import com.jikku.backend.domain.member.entity.Member;
import com.jikku.backend.domain.member.enums.SocialLogin;
import com.jikku.backend.domain.member.repository.MemberRepository;
import com.jikku.backend.global.security.DevLoginKeyVerifier;
import com.jikku.backend.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스. 지금은 카카오 없이 고정 테스트 회원으로 JWT를 발급하는 dev-login만 제공한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final DevLoginKeyVerifier devLoginKeyVerifier;

    // 고정 테스트 회원 식별값 (개발 전용). email은 NOT NULL이라 더미값을 채운다.
    private static final SocialLogin DEV_SOCIAL_LOGIN = SocialLogin.KAKAO;
    private static final String DEV_SOCIAL_UID = "dev-default";
    private static final String DEV_USERNAME = "테스트유저";
    private static final String DEV_EMAIL = "dev@jikku.local";

    /** 개발용 로그인: 보호키를 확인하고, 고정 테스트 회원을 찾거나 만들어 그 회원의 JWT를 발급한다. */
    @Transactional
    public TokenResponse devLogin(String devKey) {
        devLoginKeyVerifier.verify(devKey);

        Member member = memberRepository
                .findBySocialLoginAndSocialUid(DEV_SOCIAL_LOGIN, DEV_SOCIAL_UID)
                .orElseGet(this::createDevMember);

        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId());
        return TokenResponse.bearer(accessToken);
    }

    // 테스트 회원이 아직 없으면 생성해 저장한다.
    private Member createDevMember() {
        Member member = Member.builder()
                .socialLogin(DEV_SOCIAL_LOGIN)
                .socialUid(DEV_SOCIAL_UID)
                .username(DEV_USERNAME)
                .email(DEV_EMAIL)
                .build();
        log.info("개발용 테스트 회원 생성");
        return memberRepository.save(member);
    }
}
