package com.jikku.backend.domain.member.service;

import com.jikku.backend.domain.member.client.KakaoClient;
import com.jikku.backend.domain.member.dto.KakaoUserResponse;
import com.jikku.backend.domain.member.dto.TokenResponse;
import com.jikku.backend.domain.member.entity.Member;
import com.jikku.backend.domain.member.enums.SocialLogin;
import com.jikku.backend.domain.member.exception.MemberErrorCode;
import com.jikku.backend.domain.member.repository.MemberRepository;
import com.jikku.backend.global.exception.BaseException;
import com.jikku.backend.global.security.DevLoginKeyVerifier;
import com.jikku.backend.global.security.JwtTokenProvider;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String ACCESS_TOKEN = "kakao-access-token";
    private static final String SOCIAL_UID = "1234567890";

    @Mock
    MemberRepository memberRepository;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    DevLoginKeyVerifier devLoginKeyVerifier;
    @Mock
    KakaoClient kakaoClient;

    @InjectMocks
    AuthService authService;

    @Test
    @DisplayName("처음 로그인하는 사용자는 카카오 프로필로 가입되고 토큰을 받는다")
    void registersOnFirstLogin() {
        given(kakaoClient.getUserInfo(ACCESS_TOKEN)).willReturn(kakaoUser("여행가", "traveler@kakao.com"));
        given(memberRepository.findBySocialLoginAndSocialUid(SocialLogin.KAKAO, SOCIAL_UID))
                .willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willReturn(member(1L));
        given(jwtTokenProvider.createAccessToken(1L)).willReturn("access");
        given(jwtTokenProvider.createRefreshToken(1L)).willReturn("refresh");

        TokenResponse response = authService.kakaoLogin(ACCESS_TOKEN);

        ArgumentCaptor<Member> saved = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(saved.capture());
        assertThat(saved.getValue().getSocialLogin()).isEqualTo(SocialLogin.KAKAO);
        assertThat(saved.getValue().getSocialUid()).isEqualTo(SOCIAL_UID);
        assertThat(saved.getValue().getUsername()).isEqualTo("여행가");
        assertThat(saved.getValue().getEmail()).isEqualTo("traveler@kakao.com");

        assertThat(response.grantType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isEqualTo("access");
        assertThat(response.refreshToken()).isEqualTo("refresh");
    }

    @Test
    @DisplayName("이미 가입된 사용자는 다시 가입시키지 않는다")
    void reusesExistingMember() {
        given(kakaoClient.getUserInfo(ACCESS_TOKEN)).willReturn(kakaoUser("여행가", "traveler@kakao.com"));
        given(memberRepository.findBySocialLoginAndSocialUid(SocialLogin.KAKAO, SOCIAL_UID))
                .willReturn(Optional.of(member(7L)));
        given(jwtTokenProvider.createAccessToken(7L)).willReturn("access");
        given(jwtTokenProvider.createRefreshToken(7L)).willReturn("refresh");

        authService.kakaoLogin(ACCESS_TOKEN);

        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("닉네임 동의를 받지 못했으면 기본 이름으로 가입시킨다 (표시용이라 로그인을 막지 않는다)")
    void fallsBackToDefaultUsername() {
        given(kakaoClient.getUserInfo(ACCESS_TOKEN)).willReturn(kakaoUser(null, "traveler@kakao.com"));
        given(memberRepository.findBySocialLoginAndSocialUid(SocialLogin.KAKAO, SOCIAL_UID))
                .willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willReturn(member(1L));

        authService.kakaoLogin(ACCESS_TOKEN);

        ArgumentCaptor<Member> saved = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(saved.capture());
        assertThat(saved.getValue().getUsername()).isEqualTo("여행자");
    }

    @Test
    @DisplayName("이메일 동의를 받지 못했으면 가짜 값을 채우지 않고 거부한다")
    void rejectsWhenEmailNotAgreed() {
        given(kakaoClient.getUserInfo(ACCESS_TOKEN)).willReturn(kakaoUser("여행가", null));

        assertThatThrownBy(() -> authService.kakaoLogin(ACCESS_TOKEN))
                .isInstanceOf(BaseException.class)
                .extracting(e -> ((BaseException) e).getErrorCode())
                .isEqualTo(MemberErrorCode.KAKAO_EMAIL_NOT_AGREED);

        verify(memberRepository, never()).save(any(Member.class));
        verify(memberRepository, never()).findBySocialLoginAndSocialUid(any(), any());
    }

    private KakaoUserResponse kakaoUser(String nickname, String email) {
        KakaoUserResponse.Profile profile = nickname == null ? null : new KakaoUserResponse.Profile(nickname);
        return new KakaoUserResponse(
                Long.valueOf(SOCIAL_UID),
                new KakaoUserResponse.KakaoAccount(email, profile));
    }

    private Member member(Long memberId) {
        return Member.builder()
                .memberId(memberId)
                .socialLogin(SocialLogin.KAKAO)
                .socialUid(SOCIAL_UID)
                .username("여행가")
                .email("traveler@kakao.com")
                .build();
    }
}
