package com.jikku.backend.domain.tourApi.client;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.jikku.backend.domain.tourApi.dto.DetailItem;
import com.jikku.backend.domain.tourApi.dto.FestivalItem;
import com.jikku.backend.domain.tourApi.dto.SpotItem;
import com.jikku.backend.domain.tourApi.dto.VisitorDDItem;
import com.jikku.backend.domain.tourApi.dto.TourApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class TourApiClient {

    // 지역 지정은 법정동 코드로 한다. 구 areaCode(강원=32)는 매뉴얼 v4.4(2026-02-10)에서 스펙 삭제됐고,
    // 서버가 응답은 해도 갱신이 멈춰 있다 (강원 관광지 728건 vs lDongRegnCd 1,356건).
    private static final String GANGWON_LDONG_REGN_CD = "51";
    private static final int CONTENT_TYPE_SPOT = 12;

    // 페이지를 나눠 도는 동안 정렬이 흔들리면 경계에서 누락이 생기므로 고정한다 (C=수정일순)
    private static final String ARRANGE_MODIFIED = "C";

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

        return parse(rawJson, new TypeReference<TourApiResponse<VisitorDDItem>>() {}).items();
    }

    /**
     * 강원 관광지 목록 한 페이지. 총건수는 응답의 totalCount로 확인한다.
     *
     * @param pageNo    1부터 시작
     * @param numOfRows 한 번에 받을 행 수 (최대 1000)
     */
    public TourApiResponse<SpotItem> getSpots(int pageNo, int numOfRows) {
        String rawJson = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/KorService2/areaBasedList2")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("numOfRows", numOfRows)
                        .queryParam("pageNo", pageNo)
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", mobileApp)
                        .queryParam("lDongRegnCd", GANGWON_LDONG_REGN_CD)
                        .queryParam("contentTypeId", CONTENT_TYPE_SPOT)
                        .queryParam("arrange", ARRANGE_MODIFIED)
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .body(String.class);

        return parse(rawJson, new TypeReference<TourApiResponse<SpotItem>>() {});
    }

    /**
     * 강원 축제 목록 한 페이지. 축제 기간은 이 오퍼레이션에만 있어 areaBasedList2로 대체할 수 없다.
     *
     * @param eventStartYmd 이 날짜 이후 시작하는 축제만 "yyyyMMdd"
     */
    public TourApiResponse<FestivalItem> getFestivals(String eventStartYmd, int pageNo, int numOfRows) {
        String rawJson = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/KorService2/searchFestival2")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("numOfRows", numOfRows)
                        .queryParam("pageNo", pageNo)
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", mobileApp)
                        .queryParam("lDongRegnCd", GANGWON_LDONG_REGN_CD)
                        .queryParam("eventStartDate", eventStartYmd)
                        .queryParam("arrange", ARRANGE_MODIFIED)
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .body(String.class);

        return parse(rawJson, new TypeReference<TourApiResponse<FestivalItem>>() {});
    }

    /**
     * 관광지·축제 상세. 목록 오퍼레이션에 없는 overview를 얻는 유일한 경로이며 건당 1회 호출이 든다.
     */
    public TourApiResponse<DetailItem> getDetail(long contentId) {
        String rawJson = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/KorService2/detailCommon2")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("numOfRows", 1)
                        .queryParam("pageNo", 1)
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", mobileApp)
                        .queryParam("contentId", contentId)
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .body(String.class);

        return parse(rawJson, new TypeReference<TourApiResponse<DetailItem>>() {});
    }

    private <T> TourApiResponse<T> parse(String rawJson, TypeReference<TourApiResponse<T>> type) {
        try {
            return objectMapper.readValue(rawJson, type);
        } catch (JacksonException e) {
            // 인증키 오류 시 JSON이 아닌 응답(XML/HTML)이, 결과 0건이면 items가 객체 대신 ""로 와서
            // 둘 다 여기로 떨어진다. 원인 구분이 필요하므로 원본을 남긴다.
            log.error("TourAPI 응답 파싱 실패. 원본 응답=\n{}", rawJson, e);
            return new TourApiResponse<>(null);
        }
    }
}
