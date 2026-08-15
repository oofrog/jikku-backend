package com.jikku.backend.domain.member.client;

import com.jikku.backend.domain.member.dto.KakaoUserResponse;
import com.jikku.backend.domain.member.exception.MemberErrorCode;
import com.jikku.backend.global.exception.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class KakaoClientTest {

    private static final String API_BASE_URL = "https://kapi.kakao.com";
    private static final String AUTH_BASE_URL = "https://kauth.kakao.com";
    private static final String USER_INFO_URL = API_BASE_URL + "/v2/user/me";
    private static final String TOKEN_URL = AUTH_BASE_URL + "/oauth/token";
    private static final String ACCESS_TOKEN = "kakao-access-token";
    private static final String CODE = "kakao-auth-code";
    private static final String REDIRECT_URI = "http://localhost:5173/oauth/kakao";
    private static final String REST_API_KEY = "rest-api-key";
    private static final String CLIENT_SECRET = "client-secret";

    private MockRestServiceServer server;
    private KakaoClient kakaoClient;

    @BeforeEach
    void setUp() {
        kakaoClient = kakaoClient(CLIENT_SECRET);
    }

    // clientSecret 유무에 따라 요청 파라미터가 달라져 케이스별로 다시 만든다
    private KakaoClient kakaoClient(String clientSecret) {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        return new KakaoClient(builder, API_BASE_URL, AUTH_BASE_URL, REST_API_KEY, clientSecret);
    }

    @Test
    @DisplayName("인가 코드를 REST 키·시크릿과 함께 form으로 보내고 액세스 토큰을 받는다")
    void exchangesCodeForToken() {
        MultiValueMap<String, String> expected = new LinkedMultiValueMap<>();
        expected.add("grant_type", "authorization_code");
        expected.add("client_id", REST_API_KEY);
        expected.add("redirect_uri", REDIRECT_URI);
        expected.add("code", CODE);
        expected.add("client_secret", CLIENT_SECRET);

        server.expect(requestTo(TOKEN_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().formData(expected))
                .andRespond(withSuccess("""
                        { "token_type": "bearer", "access_token": "kakao-access-token", "expires_in": 21599 }
                        """, MediaType.APPLICATION_JSON));

        assertThat(kakaoClient.getToken(CODE, REDIRECT_URI)).isEqualTo(ACCESS_TOKEN);
        server.verify();
    }

    @Test
    @DisplayName("Client Secret을 끈 앱은 그 파라미터를 아예 보내지 않는다 (보내면 카카오가 거부한다)")
    void omitsClientSecretWhenBlank() {
        KakaoClient client = kakaoClient("");

        MultiValueMap<String, String> expected = new LinkedMultiValueMap<>();
        expected.add("grant_type", "authorization_code");
        expected.add("client_id", REST_API_KEY);
        expected.add("redirect_uri", REDIRECT_URI);
        expected.add("code", CODE);

        server.expect(requestTo(TOKEN_URL))
                .andExpect(content().formData(expected))
                .andRespond(withSuccess("{ \"access_token\": \"kakao-access-token\" }", MediaType.APPLICATION_JSON));

        assertThat(client.getToken(CODE, REDIRECT_URI)).isEqualTo(ACCESS_TOKEN);
        server.verify();
    }

    @Test
    @DisplayName("만료·재사용된 코드는 사용자 토큰 문제와 다른 코드로 구분해 던진다")
    void throwsInvalidCodeOn4xx() {
        server.expect(requestTo(TOKEN_URL))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body("{\"error\":\"invalid_grant\",\"error_code\":\"KOE320\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> kakaoClient.getToken(CODE, REDIRECT_URI))
                .isInstanceOf(BaseException.class)
                .extracting(e -> ((BaseException) e).getErrorCode())
                .isEqualTo(MemberErrorCode.INVALID_KAKAO_CODE);
    }

    @Test
    @DisplayName("200인데 access_token이 없으면 실패로 본다")
    void throwsWhenAccessTokenMissing() {
        server.expect(requestTo(TOKEN_URL))
                .andRespond(withSuccess("{ \"token_type\": \"bearer\" }", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> kakaoClient.getToken(CODE, REDIRECT_URI))
                .isInstanceOf(BaseException.class)
                .extracting(e -> ((BaseException) e).getErrorCode())
                .isEqualTo(MemberErrorCode.KAKAO_SERVER_ERROR);
    }

    @Test
    @DisplayName("액세스 토큰을 Bearer 헤더로 보내고 회원번호·닉네임·이메일을 읽는다")
    void readsUserInfo() {
        server.expect(requestTo(USER_INFO_URL))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN))
                .andRespond(withSuccess("""
                        {
                          "id": 1234567890,
                          "kakao_account": {
                            "email": "traveler@kakao.com",
                            "profile": { "nickname": "여행가" }
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        KakaoUserResponse response = kakaoClient.getUserInfo(ACCESS_TOKEN);

        assertThat(response.socialUid()).isEqualTo("1234567890");
        assertThat(response.nickname()).isEqualTo("여행가");
        assertThat(response.email()).isEqualTo("traveler@kakao.com");
        server.verify();
    }

    @Test
    @DisplayName("동의하지 않은 항목은 키 자체가 빠져 오므로 null로 읽힌다")
    void readsNullWhenConsentMissing() {
        server.expect(requestTo(USER_INFO_URL))
                .andRespond(withSuccess("""
                        { "id": 42, "kakao_account": { "profile_needs_agreement": true } }
                        """, MediaType.APPLICATION_JSON));

        KakaoUserResponse response = kakaoClient.getUserInfo(ACCESS_TOKEN);

        assertThat(response.socialUid()).isEqualTo("42");
        assertThat(response.nickname()).isNull();
        assertThat(response.email()).isNull();
    }

    @Test
    @DisplayName("카카오가 401을 주면 사용자 토큰 문제로 구분해 던진다")
    void throwsInvalidTokenOn401() {
        server.expect(requestTo(USER_INFO_URL))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .body("{\"msg\":\"this access token does not exist\",\"code\":-401}")
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> kakaoClient.getUserInfo(ACCESS_TOKEN))
                .isInstanceOf(BaseException.class)
                .extracting(e -> ((BaseException) e).getErrorCode())
                .isEqualTo(MemberErrorCode.INVALID_KAKAO_TOKEN);
    }

    @Test
    @DisplayName("카카오 장애(5xx)는 토큰 문제와 다른 코드로 나간다")
    void throwsServerErrorOn5xx() {
        server.expect(requestTo(USER_INFO_URL))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThatThrownBy(() -> kakaoClient.getUserInfo(ACCESS_TOKEN))
                .isInstanceOf(BaseException.class)
                .extracting(e -> ((BaseException) e).getErrorCode())
                .isEqualTo(MemberErrorCode.KAKAO_SERVER_ERROR);
    }

    @Test
    @DisplayName("200인데 회원번호가 없으면 실패로 본다 (social_uid가 null인 회원이 생기면 안 된다)")
    void throwsWhenIdMissing() {
        server.expect(requestTo(USER_INFO_URL))
                .andRespond(withSuccess("{ \"kakao_account\": {} }", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> kakaoClient.getUserInfo(ACCESS_TOKEN))
                .isInstanceOf(BaseException.class)
                .extracting(e -> ((BaseException) e).getErrorCode())
                .isEqualTo(MemberErrorCode.KAKAO_SERVER_ERROR);
    }
}
