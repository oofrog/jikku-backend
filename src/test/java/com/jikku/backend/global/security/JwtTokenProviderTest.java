package com.jikku.backend.global.security;

import com.jikku.backend.global.exception.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-key-for-jikku-backend-hs256-minimum-32bytes";
    private static final long ONE_HOUR = 3_600_000L;
    private static final long FOURTEEN_DAYS = 1_209_600_000L;

    private final JwtTokenProvider provider = new JwtTokenProvider(SECRET, ONE_HOUR, FOURTEEN_DAYS);

    @Test
    @DisplayName("Access 토큰은 검증을 통과하고 memberId를 되돌려준다")
    void accessTokenRoundTrip() {
        String token = provider.createAccessToken(42L);

        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.getMemberId(token)).isEqualTo(42L);
    }

    @Test
    @DisplayName("Refresh 토큰은 Access 토큰 자리에 쓸 수 없다")
    void refreshTokenIsNotAccepted() {
        String refreshToken = provider.createRefreshToken(42L);

        // 같은 키로 서명돼 서명 검증만으로는 통과하므로, 용도(typ) 구분이 실제로 막고 있는지 확인한다
        assertThat(provider.validateToken(refreshToken)).isFalse();
    }

    @Test
    @DisplayName("Access 토큰으로는 재발급받을 수 없다")
    void accessTokenCannotBeReissued() {
        String accessToken = provider.createAccessToken(42L);

        assertThatThrownBy(() -> provider.getMemberIdFromRefreshToken(accessToken))
                .isInstanceOf(BaseException.class);
    }

    @Test
    @DisplayName("Refresh 토큰에서 memberId를 꺼낸다")
    void refreshTokenRoundTrip() {
        assertThat(provider.getMemberIdFromRefreshToken(provider.createRefreshToken(7L))).isEqualTo(7L);
    }

    @Test
    @DisplayName("다른 키로 서명된 토큰은 거부한다")
    void rejectsForeignSignature() {
        String forged = new JwtTokenProvider(
                "another-secret-key-that-is-also-long-enough-32bytes", ONE_HOUR, FOURTEEN_DAYS)
                .createRefreshToken(42L);

        assertThat(provider.validateToken(forged)).isFalse();
        assertThatThrownBy(() -> provider.getMemberIdFromRefreshToken(forged))
                .isInstanceOf(BaseException.class);
    }

    @Test
    @DisplayName("만료된 Refresh 토큰은 만료로 구분해 알린다")
    void expiredRefreshTokenIsDistinguished() {
        String expired = new JwtTokenProvider(SECRET, ONE_HOUR, -1000L).createRefreshToken(42L);

        assertThatThrownBy(() -> provider.getMemberIdFromRefreshToken(expired))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining("만료");
    }
}
