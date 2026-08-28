package com.jikku.backend.domain.member.service;

import com.jikku.backend.domain.member.client.KakaoClient;
import com.jikku.backend.domain.member.dto.KakaoUserResponse;
import com.jikku.backend.domain.member.dto.TokenResponse;
import com.jikku.backend.domain.member.entity.Member;
import com.jikku.backend.domain.member.enums.SocialLogin;
import com.jikku.backend.domain.member.exception.MemberErrorCode;
import com.jikku.backend.domain.member.repository.MemberRepository;
import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import com.jikku.backend.global.exception.BaseException;
import com.jikku.backend.global.security.DevLoginKeyVerifier;
import com.jikku.backend.global.security.JwtTokenProvider;
import com.jikku.backend.global.security.TokenHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 인증 서비스. 카카오 로그인과, 카카오 없이 고정 테스트 회원으로 JWT를 발급하는 dev-login을 제공한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final DevLoginKeyVerifier devLoginKeyVerifier;
    private final KakaoClient kakaoClient;
    private final TokenHasher tokenHasher;

    // 고정 테스트 회원 식별값 (개발 전용). email은 NOT NULL이라 더미값을 채운다.
    private static final SocialLogin DEV_SOCIAL_LOGIN = SocialLogin.KAKAO;
    private static final String DEV_SOCIAL_UID = "dev-default";
    private static final String DEV_USERNAME = "테스트유저";
    private static final String DEV_EMAIL = "dev@jikku.local";

    // 닉네임 동의를 받지 못했을 때 쓸 표시용 이름. username도 NOT NULL이다.
    private static final String DEFAULT_USERNAME = "여행자";

    /**
     * 카카오 로그인: 프론트가 보낸 인가 코드를 카카오 액세스 토큰으로 바꾸고, 그 토큰으로 사용자 정보를
     * 조회해 검증한 뒤, 그 회원을 찾거나 새로 만들어 우리 JWT를 발급한다.
     *
     * <p>메서드 전체를 하나의 트랜잭션으로 묶지 않는다. 아래 중복 가입 처리가 "insert 실패 → 재조회"인데,
     * 한 트랜잭션 안에서 제약 위반이 나면 그 트랜잭션엔 이미 롤백 표시가 붙어 재조회 결과를 커밋할 수 없다.
     * 리포지토리 호출마다 트랜잭션이 따로 열리게 두면 실패한 insert만 롤백되고 재조회는 새 트랜잭션에서 성공한다.
     */
    public TokenResponse kakaoLogin(String code, String redirectUri) {
        String kakaoAccessToken = kakaoClient.getToken(code, redirectUri);
        KakaoUserResponse kakaoUser = kakaoClient.getUserInfo(kakaoAccessToken);

        // 이메일은 카카오 동의항목이라 빠질 수 있는데 member.email은 NOT NULL이다.
        // 가짜 주소를 채워 넣는 대신 명시적으로 거부해, 프론트가 동의를 다시 받게 한다.
        if (!StringUtils.hasText(kakaoUser.email())) {
            throw new BaseException(MemberErrorCode.KAKAO_EMAIL_NOT_AGREED);
        }

        Member member = memberRepository
                .findBySocialLoginAndSocialUid(SocialLogin.KAKAO, kakaoUser.socialUid())
                .orElseGet(() -> registerKakaoMember(kakaoUser));

        return issueTokens(member);
    }

    /**
     * 첫 로그인 요청이 동시에 두 번 들어오면 둘 다 조회에 실패해 같은 회원을 두 번 insert하려 한다.
     * (social_login, social_uid) 유니크 제약이 한쪽을 막아주므로, 그 실패는 오류가 아니라
     * "다른 요청이 먼저 만들었다"는 신호로 보고 다시 조회한다.
     */
    private Member registerKakaoMember(KakaoUserResponse kakaoUser) {
        Member member = Member.builder()
                .socialLogin(SocialLogin.KAKAO)
                .socialUid(kakaoUser.socialUid())
                .username(StringUtils.hasText(kakaoUser.nickname()) ? kakaoUser.nickname() : DEFAULT_USERNAME)
                .email(kakaoUser.email())
                .build();

        try {
            log.info("카카오 신규 회원 가입");
            return memberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            return memberRepository
                    .findBySocialLoginAndSocialUid(SocialLogin.KAKAO, kakaoUser.socialUid())
                    .orElseThrow(() -> e);
        }
    }

    /** 개발용 로그인: 보호키를 확인하고, 고정 테스트 회원을 찾거나 만들어 그 회원의 JWT를 발급한다. */
    @Transactional
    public TokenResponse devLogin(String devKey) {
        devLoginKeyVerifier.verify(devKey);

        Member member = memberRepository
                .findBySocialLoginAndSocialUid(DEV_SOCIAL_LOGIN, DEV_SOCIAL_UID)
                .orElseGet(this::createDevMember);

        return issueTokens(member);
    }

    /**
     * Refresh 토큰으로 토큰 쌍을 재발급한다.
     * 서명·만료가 멀쩡해도 서버에 저장된 것과 다르면 거부한다 — 로그아웃했거나 다른 기기 로그인에 밀린 토큰이다.
     */
    @Transactional
    public TokenResponse reissue(String refreshToken) {
        Long memberId = jwtTokenProvider.getMemberIdFromRefreshToken(refreshToken);

        // 서명이 유효해도 탈퇴 등으로 회원이 사라졌을 수 있다
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (!tokenHasher.hash(refreshToken).equals(member.getRefreshToken())) {
            throw new BaseException(GeneralErrorCode.REVOKED_TOKEN);
        }

        return issueTokens(member);
    }

    /** 로그아웃: 저장된 Refresh 토큰을 지운다. 이미 발급된 Access 토큰은 만료(1시간)까지 남는다. */
    @Transactional
    public void logout(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(MemberErrorCode.MEMBER_NOT_FOUND))
                .clearRefreshToken();
    }

    /**
     * 새로 발급한 Refresh 토큰의 해시를 회원에 심는다. 이 시점부터 이전 Refresh 토큰은 무효다.
     * 트랜잭션 밖에서 불릴 수 있어(카카오 로그인) 준영속 상태를 가정하고 명시적으로 저장한다.
     */
    private TokenResponse issueTokens(Member member) {
        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberId());

        member.updateRefreshToken(tokenHasher.hash(refreshToken));
        memberRepository.save(member);

        return TokenResponse.bearer(accessToken, refreshToken);
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
