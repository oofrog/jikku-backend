package com.jikku.backend.domain.member.client;

import com.jikku.backend.domain.member.dto.KakaoUserResponse;
import com.jikku.backend.domain.member.exception.MemberErrorCode;
import com.jikku.backend.global.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * 카카오 사용자 정보 조회. 프론트가 카카오 SDK로 받아온 액세스 토큰을 그대로 넘겨받아 쓴다.
 * 인가 코드 교환 방식이 아니라서 REST API 키·client secret이 필요 없다 — 이 호출의 인증은 사용자 토큰 자체다.
 */
@Slf4j
@Component
public class KakaoClient {

    private static final String USER_INFO_PATH = "/v2/user/me";

    private final RestClient restClient;

    // 주입받은 빌더로 만든다. 내부에서 RestClient를 직접 생성하면 테스트에서 호출을 가로챌 수 없다.
    public KakaoClient(RestClient.Builder builder, @Value("${kakao.api-base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    /**
     * 액세스 토큰으로 카카오 사용자 정보를 조회한다. 조회에 성공한 것 자체가 토큰 검증을 겸한다.
     *
     * @throws BaseException 토큰이 유효하지 않거나(KAKAO401_1) 카카오 호출이 실패한 경우(KAKAO502_1)
     */
    public KakaoUserResponse getUserInfo(String kakaoAccessToken) {
        KakaoUserResponse response;

        try {
            response = restClient.get()
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
