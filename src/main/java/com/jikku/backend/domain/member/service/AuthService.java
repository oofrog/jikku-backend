package com.jikku.backend.domain.member.service;

import com.jikku.backend.domain.member.dto.TokenResponse;
import com.jikku.backend.domain.member.entity.Member;
import com.jikku.backend.domain.member.enums.SocialLogin;
import com.jikku.backend.domain.member.repository.MemberRepository;
import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import com.jikku.backend.global.exception.BaseException;
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

        return issueTokens(member.getMemberId());
    }

    /**
     * Refresh 토큰으로 토큰 쌍을 재발급한다.
     * 서버에 저장하지 않는 방식이라 이전 Refresh 토큰은 만료 전까지 계속 유효하다 — 회전은 되지 않는다.
     */
    @Transactional(readOnly = true)
    public TokenResponse reissue(String refreshToken) {
        Long memberId = jwtTokenProvider.getMemberIdFromRefreshToken(refreshToken);

        // 서명이 유효해도 탈퇴 등으로 회원이 사라졌을 수 있다
        if (!memberRepository.existsById(memberId)) {
            throw new BaseException(GeneralErrorCode.MEMBER_NOT_FOUND);
        }

        return issueTokens(memberId);
    }

    private TokenResponse issueTokens(Long memberId) {
        return TokenResponse.bearer(
                jwtTokenProvider.createAccessToken(memberId),
                jwtTokenProvider.createRefreshToken(memberId));
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
