package com.jikku.backend.domain.tourApi.client;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.jikku.backend.domain.tourApi.dto.VisitorDDItem;
import com.jikku.backend.domain.tourApi.dto.TourApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component   // 스프링 빈으로 등록 → 다음 단계 test 가 주입받아 쓴다
public class TourApiClient {

    private final RestClient restClient;      // 실제 HTTP 호출 담당
    private final ObjectMapper objectMapper;  // JSON 문자열 → 객체 변환 담당
    private final String serviceKey;
    private final String mobileApp;

    // 생성자: 스프링이 application.yml 값(@Value)과 ObjectMapper(자동 빈)를 넣어준다.
    public TourApiClient(
            ObjectMapper objectMapper,
            @Value("${tour-api.base-url}") String baseUrl,
            @Value("${tour-api.service-key}") String serviceKey,
            @Value("${tour-api.mobile-app}") String mobileApp
    ) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.serviceKey = serviceKey;
        this.mobileApp = mobileApp;
    }

    /**
     * 하루치 기초지자체 방문자수를 호출해 item 리스트로 돌려준다.
     * 2단계에선 강원 필터 없이 "받아서 변환"까지만 한다.
     *
     * @param baseYmd   기준일 "yyyyMMdd" (예: "20250623")
     * @param numOfRows 한 번에 받을 행 수 (2단계 검증용은 작게, 예: 10)
     */
    public List<VisitorDDItem> getVisitorData(String baseYmd, int numOfRows) {
        // 1) 응답을 '문자열 그대로' 받는다 (자동 변환을 일부러 안 씀)
        String rawJson = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/DataLabService/locgoRegnVisitrDDList")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("numOfRows", numOfRows)
                        .queryParam("pageNo", 1)
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", mobileApp)
                        .queryParam("startYmd", baseYmd)
                        .queryParam("endYmd", baseYmd)
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .body(String.class);

        // 2) 문자열 JSON 을 TourApiResponse<VisitorDDItem> 로 직접 변환한다.
        try {
            TourApiResponse<VisitorDDItem> response = objectMapper.readValue(
                    rawJson,
                    new TypeReference<TourApiResponse<VisitorDDItem>>() {}
            );
            return response.items();   // 공용 도우미로 item 리스트만 꺼냄
        } catch (JacksonException e) {
            // 인증키 오류 등으로 JSON 이 아닌 응답(XML/HTML)이 오면 여기로 온다.
            // 원본을 통째로 찍어두면 '무엇이 왔는지' 바로 확인 가능.
            log.error("TourAPI 응답 파싱 실패. 원본 응답=\n{}", rawJson, e);
            return Collections.emptyList();
        }
    }
}