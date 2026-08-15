package com.jikku.backend.domain.member.client;

import com.jikku.backend.domain.member.dto.KakaoTokenResponse;
import com.jikku.backend.domain.member.dto.KakaoUserResponse;
import com.jikku.backend.domain.member.exception.MemberErrorCode;
import com.jikku.backend.global.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * 카카오 인증 서버 호출. 토큰 발급(kauth)과 사용자 정보 조회(kapi)는 호스트가 달라 클라이언트를 둘 둔다.
 *
 * <p>프론트는 인가 코드까지만 받는다 — JS SDK v2가 보안상 클라이언트 토큰 발급(팝업 로그인)을 없앴고,
 * 코드→토큰 교환은 REST API 키가 필요해 서버만 할 수 있다.
 */
@Slf4j
@Component
public class KakaoClient {

    private static final String TOKEN_PATH = "/oauth/token";
    private static final String USER_INFO_PATH = "/v2/user/me";
    private static final String GRANT_TYPE = "authorization_code";

    private final RestClient authClient;
    private final RestClient apiClient;
    private final String restApiKey;
    private final String clientSecret;

    // 주입받은 빌더로 만든다. 내부에서 RestClient를 직접 생성하면 테스트에서 호출을 가로챌 수 없다.
    // 빌더는 가변이라 baseUrl만 바꿔 두 번 build하면 설정이 섞일 수 있어 clone으로 갈라 쓴다.
    public KakaoClient(RestClient.Builder builder,
                       @Value("${kakao.api-base-url}") String apiBaseUrl,
                       @Value("${kakao.auth-base-url}") String authBaseUrl,
                       @Value("${kakao.rest-api-key}") String restApiKey,
                       @Value("${kakao.client-secret:}") String clientSecret) {
        this.apiClient = builder.clone().baseUrl(apiBaseUrl).build();
        this.authClient = builder.clone().baseUrl(authBaseUrl).build();
        this.restApiKey = restApiKey;
        this.clientSecret = clientSecret;
    }

    /**
     * 인가 코드를 카카오 액세스 토큰으로 교환한다.
     *
     * @param redirectUri 프론트가 인가 요청에 쓴 값과 문자 단위로 같아야 한다. 다르면 카카오가 거부한다.
     * @throws BaseException 코드가 유효하지 않거나(KAKAO401_2) 카카오 호출이 실패한 경우(KAKAO502_1)
     */
    public String getToken(String code, String redirectUri) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", GRANT_TYPE);
        form.add("client_id", restApiKey);
        form.add("redirect_uri", redirectUri);
        form.add("code", code);
        // 콘솔에서 Client Secret을 끈 앱은 이 파라미터를 보내면 안 된다
        if (StringUtils.hasText(clientSecret)) {
            form.add("client_secret", clientSecret);
        }

        KakaoTokenResponse response;

        try {
            response = authClient.post()
                    .uri(TOKEN_PATH)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    // 코드 문제(만료·재사용·redirect_uri 불일치)는 400으로 오므로 우리 서버 장애와 갈라낸다
                    .onStatus(HttpStatusCode::is4xxClientError, (request, clientResponse) -> {
                        log.warn("카카오 토큰 발급 거부. status={}", clientResponse.getStatusCode());
                        throw new BaseException(MemberErrorCode.INVALID_KAKAO_CODE);
                    })
                    .body(KakaoTokenResponse.class);
        } catch (RestClientException e) {
            log.error("카카오 토큰 발급 실패", e);
            throw new BaseException(MemberErrorCode.KAKAO_SERVER_ERROR);
        }

        if (response == null || !StringUtils.hasText(response.accessToken())) {
            log.error("카카오 토큰 응답에 access_token이 없다");
            throw new BaseException(MemberErrorCode.KAKAO_SERVER_ERROR);
        }

        return response.accessToken();
    }

    /**
     * 액세스 토큰으로 카카오 사용자 정보를 조회한다. 조회에 성공한 것 자체가 토큰 검증을 겸한다.
     *
     * @throws BaseException 토큰이 유효하지 않거나(KAKAO401_1) 카카오 호출이 실패한 경우(KAKAO502_1)
     */
    public KakaoUserResponse getUserInfo(String kakaoAccessToken) {
        KakaoUserResponse response;

        try {
            response = apiClient.get()
                    .uri(USER_INFO_PATH)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                    .retrieve()
                    // 토큰이 틀리거나 만료되면 카카오가 401을 준다. 기본 핸들러에 맡기면 5xx와 같은
                    // 예외 계열로 뭉개져서 "사용자 토큰 문제"가 "우리 서버 장애"로 보인다.
                    .onStatus(HttpStatusCode::is4xxClientError, (request, clientResponse) -> {
                        log.warn("카카오 사용자 정보 조회 거부. status={}", clientResponse.getStatusCode());
                        throw new BaseException(MemberErrorCode.INVALID_KAKAO_TOKEN);
                    })
                    .body(KakaoUserResponse.class);
        } catch (RestClientException e) {
            log.error("카카오 사용자 정보 조회 실패", e);
            throw new BaseException(MemberErrorCode.KAKAO_SERVER_ERROR);
        }

        // 200인데 회원번호가 없으면 우리가 아는 스펙이 아니다. 그대로 두면 social_uid가 null인 회원이 생긴다.
        if (response == null || response.socialUid() == null) {
            log.error("카카오 응답에 사용자 id가 없다. response={}", response);
            throw new BaseException(MemberErrorCode.KAKAO_SERVER_ERROR);
        }

        return response;
    }
}
