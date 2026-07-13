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
@Component
public class TourApiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String serviceKey;
    private final String mobileApp;

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
     *
     * @param baseYmd   기준일 "yyyyMMdd"
     * @param numOfRows 한 번에 받을 행 수
     */
    public List<VisitorDDItem> getVisitorData(String baseYmd, int numOfRows) {
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

        try {
            TourApiResponse<VisitorDDItem> response = objectMapper.readValue(
                    rawJson,
                    new TypeReference<TourApiResponse<VisitorDDItem>>() {}
            );
            return response.items();
        } catch (JacksonException e) {
            // 인증키 오류 등으로 JSON이 아닌 응답(XML/HTML)이 올 수 있어 원본을 남긴다
            log.error("TourAPI 응답 파싱 실패. 원본 응답=\n{}", rawJson, e);
            return Collections.emptyList();
        }
    }
}