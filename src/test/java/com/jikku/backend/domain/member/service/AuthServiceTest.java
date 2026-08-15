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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String CODE = "kakao-auth-code";
    private static final String REDIRECT_URI = "http://localhost:5173/oauth/kakao";
    private static final String ACCESS_TOKEN = "kakao-access-token";
    private static final String SOCIAL_UID = "1234567890";
    private static final String REFRESH = "refresh";
    private static final String REFRESH_HASH = "hashed-refresh";

    @Mock
    MemberRepository memberRepository;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    DevLoginKeyVerifier devLoginKeyVerifier;
    @Mock
    KakaoClient kakaoClient;
    @Mock
    TokenHasher tokenHasher;

    @InjectMocks
    AuthService authService;

    @Test
    @DisplayName("처음 로그인하는 사용자는 카카오 프로필로 가입되고 토큰을 받는다")
    void registersOnFirstLogin() {
        given(kakaoClient.getToken(CODE, REDIRECT_URI)).willReturn(ACCESS_TOKEN);
        given(kakaoClient.getUserInfo(ACCESS_TOKEN)).willReturn(kakaoUser("여행가", "traveler@kakao.com"));
        given(memberRepository.findBySocialLoginAndSocialUid(SocialLogin.KAKAO, SOCIAL_UID))
                .willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willReturn(member(1L));
        given(jwtTokenProvider.createAccessToken(1L)).willReturn("access");
        given(jwtTokenProvider.createRefreshToken(1L)).willReturn(REFRESH);
        given(tokenHasher.hash(REFRESH)).willReturn(REFRESH_HASH);

        TokenResponse response = authService.kakaoLogin(CODE, REDIRECT_URI);

        // 가입 저장 + 발급된 Refresh 해시 저장으로 두 번 불린다
        ArgumentCaptor<Member> saved = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository, times(2)).save(saved.capture());
        Member registered = saved.getAllValues().getFirst();
        assertThat(registered.getSocialLogin()).isEqualTo(SocialLogin.KAKAO);
        assertThat(registered.getSocialUid()).isEqualTo(SOCIAL_UID);
        assertThat(registered.getUsername()).isEqualTo("여행가");
        assertThat(registered.getEmail()).isEqualTo("traveler@kakao.com");

        assertThat(response.grantType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isEqualTo("access");
        assertThat(response.refreshToken()).isEqualTo(REFRESH);
    }

    @Test
    @DisplayName("발급한 Refresh 토큰은 원문이 아니라 해시로 저장된다")
    void storesHashedRefreshToken() {
        given(kakaoClient.getToken(CODE, REDIRECT_URI)).willReturn(ACCESS_TOKEN);
        given(kakaoClient.getUserInfo(ACCESS_TOKEN)).willReturn(kakaoUser("여행가", "traveler@kakao.com"));
        given(memberRepository.findBySocialLoginAndSocialUid(SocialLogin.KAKAO, SOCIAL_UID))
                .willReturn(Optional.of(member(7L)));
        given(jwtTokenProvider.createRefreshToken(7L)).willReturn(REFRESH);
        given(tokenHasher.hash(REFRESH)).willReturn(REFRESH_HASH);

        authService.kakaoLogin(CODE, REDIRECT_URI);

        ArgumentCaptor<Member> saved = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(saved.capture());
        assertThat(saved.getValue().getRefreshToken()).isEqualTo(REFRESH_HASH);
        assertThat(saved.getValue().getRefreshToken()).isNotEqualTo(REFRESH);
    }

    @Test
    @DisplayName("이미 가입된 사용자는 다시 가입시키지 않는다")
    void reusesExistingMember() {
        given(kakaoClient.getToken(CODE, REDIRECT_URI)).willReturn(ACCESS_TOKEN);
        given(kakaoClient.getUserInfo(ACCESS_TOKEN)).willReturn(kakaoUser("여행가", "traveler@kakao.com"));
        given(memberRepository.findBySocialLoginAndSocialUid(SocialLogin.KAKAO, SOCIAL_UID))
                .willReturn(Optional.of(member(7L)));
        given(jwtTokenProvider.createAccessToken(7L)).willReturn("access");
        given(jwtTokenProvider.createRefreshToken(7L)).willReturn(REFRESH);

        authService.kakaoLogin(CODE, REDIRECT_URI);

        // 저장은 Refresh 해시 갱신 한 번뿐이고, 그 대상은 기존 회원이다 (새 회원이면 id가 없다)
        ArgumentCaptor<Member> saved = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(saved.capture());
        assertThat(saved.getValue().getMemberId()).isEqualTo(7L);
    }

    @Test
    @DisplayName("닉네임 동의를 받지 못했으면 기본 이름으로 가입시킨다 (표시용이라 로그인을 막지 않는다)")
    void fallsBackToDefaultUsername() {
        given(kakaoClient.getToken(CODE, REDIRECT_URI)).willReturn(ACCESS_TOKEN);
        given(kakaoClient.getUserInfo(ACCESS_TOKEN)).willReturn(kakaoUser(null, "traveler@kakao.com"));
        given(memberRepository.findBySocialLoginAndSocialUid(SocialLogin.KAKAO, SOCIAL_UID))
                .willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willReturn(member(1L));

        authService.kakaoLogin(CODE, REDIRECT_URI);

        ArgumentCaptor<Member> saved = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository, times(2)).save(saved.capture());
        assertThat(saved.getAllValues().getFirst().getUsername()).isEqualTo("여행자");
    }

    @Test
    @DisplayName("이메일 동의를 받지 못했으면 가짜 값을 채우지 않고 거부한다")
    void rejectsWhenEmailNotAgreed() {
        given(kakaoClient.getToken(CODE, REDIRECT_URI)).willReturn(ACCESS_TOKEN);
        given(kakaoClient.getUserInfo(ACCESS_TOKEN)).willReturn(kakaoUser("여행가", null));

        assertThatThrownBy(() -> authService.kakaoLogin(CODE, REDIRECT_URI))
                .isInstanceOf(BaseException.class)
                .extracting(e -> ((BaseException) e).getErrorCode())
                .isEqualTo(MemberErrorCode.KAKAO_EMAIL_NOT_AGREED);

        verify(memberRepository, never()).save(any(Member.class));
        verify(memberRepository, never()).findBySocialLoginAndSocialUid(any(), any());
    }

    @Test
    @DisplayName("저장된 해시와 일치하는 Refresh 토큰만 재발급된다")
    void reissuesWhenStoredHashMatches() {
        given(jwtTokenProvider.getMemberIdFromRefreshToken(REFRESH)).willReturn(7L);
        given(memberRepository.findById(7L)).willReturn(Optional.of(member(7L, REFRESH_HASH)));
        given(tokenHasher.hash(REFRESH)).willReturn(REFRESH_HASH);
        given(jwtTokenProvider.createAccessToken(7L)).willReturn("new-access");
        given(jwtTokenProvider.createRefreshToken(7L)).willReturn("new-refresh");
        given(tokenHasher.hash("new-refresh")).willReturn("hashed-new-refresh");

        TokenResponse response = authService.reissue(REFRESH);

        assertThat(response.accessToken()).isEqualTo("new-access");
        assertThat(response.refreshToken()).isEqualTo("new-refresh");

        // 재발급하면 이전 Refresh 토큰은 저장값이 바뀌어 자동으로 무효가 된다
        ArgumentCaptor<Member> saved = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(saved.capture());
        assertThat(saved.getValue().getRefreshToken()).isEqualTo("hashed-new-refresh");
    }

    @Test
    @DisplayName("로그아웃 뒤 남은 Refresh 토큰은 서명이 멀쩡해도 재발급되지 않는다")
    void rejectsReissueWhenTokenRevoked() {
        given(jwtTokenProvider.getMemberIdFromRefreshToken(REFRESH)).willReturn(7L);
        given(memberRepository.findById(7L)).willReturn(Optional.of(member(7L, null)));
        given(tokenHasher.hash(REFRESH)).willReturn(REFRESH_HASH);

        assertThatThrownBy(() -> authService.reissue(REFRESH))
                .isInstanceOf(BaseException.class)
                .extracting(e -> ((BaseException) e).getErrorCode())
                .isEqualTo(GeneralErrorCode.REVOKED_TOKEN);

        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("다른 기기에서 로그인해 밀려난 Refresh 토큰도 거부된다")
    void rejectsReissueWhenSupersededByAnotherLogin() {
        given(jwtTokenProvider.getMemberIdFromRefreshToken(REFRESH)).willReturn(7L);
        given(memberRepository.findById(7L)).willReturn(Optional.of(member(7L, "hash-from-another-device")));
        given(tokenHasher.hash(REFRESH)).willReturn(REFRESH_HASH);

        assertThatThrownBy(() -> authService.reissue(REFRESH))
                .isInstanceOf(BaseException.class)
                .extracting(e -> ((BaseException) e).getErrorCode())
                .isEqualTo(GeneralErrorCode.REVOKED_TOKEN);
    }

    @Test
    @DisplayName("로그아웃은 저장된 Refresh 토큰을 지운다")
    void logoutClearsStoredToken() {
        Member member = member(7L, REFRESH_HASH);
        given(memberRepository.findById(7L)).willReturn(Optional.of(member));

        authService.logout(7L);

        assertThat(member.getRefreshToken()).isNull();
    }

    private KakaoUserResponse kakaoUser(String nickname, String email) {
        KakaoUserResponse.Profile profile = nickname == null ? null : new KakaoUserResponse.Profile(nickname);
        return new KakaoUserResponse(
                Long.valueOf(SOCIAL_UID),
                new KakaoUserResponse.KakaoAccount(email, profile));
    }

    private Member member(Long memberId) {
        return member(memberId, null);
    }

    private Member member(Long memberId, String refreshTokenHash) {
        return Member.builder()
                .memberId(memberId)
                .socialLogin(SocialLogin.KAKAO)
                .socialUid(SOCIAL_UID)
                .username("여행가")
                .email("traveler@kakao.com")
                .refreshToken(refreshTokenHash)
                .build();
    }
}
