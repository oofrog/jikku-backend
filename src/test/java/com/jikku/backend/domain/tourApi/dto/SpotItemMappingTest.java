package com.jikku.backend.domain.tourApi.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// lDongRegnCd처럼 소문자 l + 대문자 D로 시작하는 키는 매핑이 어긋나기 쉬워 실제 응답 형태로 고정해둔다
class SpotItemMappingTest {

    private final JsonMapper mapper = JsonMapper.builder().build();

    private static final String SPOT_JSON = """
            {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},"body":{
              "items":{"item":[{
                "addr1":"강원특별자치도 홍천군 두촌면 부채들길 29",
                "contentid":"2761729","contenttypeid":"12",
                "firstimage":"http://tong.visitkorea.or.kr/cms/resource/68/2762468_image2_1.JPG",
                "mapx":"128.0184630062","mapy":"37.8364309102",
                "modifiedtime":"20251117152045","title":"가람리조트",
                "sigungucode":"16","areacode":"32",
                "lDongRegnCd":"51","lDongSignguCd":"720",
                "lclsSystm1":"AC","lclsSystm2":"AC01","lclsSystm3":"AC010200"
              }]},"numOfRows":1,"pageNo":1,"totalCount":1356}}}
            """;

    @Test
    @DisplayName("관광지 응답을 파싱하면 법정동 코드가 시군구코드로 결합된다")
    void parseSpot() {
        TourApiResponse<SpotItem> response =
                mapper.readValue(SPOT_JSON, new TypeReference<TourApiResponse<SpotItem>>() {});

        assertThat(response.totalCount()).isEqualTo(1356);
        assertThat(response.items()).hasSize(1);

        SpotItem item = response.items().getFirst();
        assertThat(item.contentid()).isEqualTo("2761729");
        assertThat(item.title()).isEqualTo("가람리조트");
        assertThat(item.lDongRegnCd()).isEqualTo("51");
        assertThat(item.lDongSignguCd()).isEqualTo("720");
        // 홍천군 = 51720, sigungu 테이블과 그대로 맞아야 한다
        assertThat(item.sigunguCd()).isEqualTo(51720);
        assertThat(item.lclsSystm3()).isEqualTo("AC010200");
    }

    private static final String FESTIVAL_JSON = """
            {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},"body":{
              "items":{"item":[{
                "addr1":"강원특별자치도 강릉시 경포로 515","contentid":"695592",
                "contenttypeid":"15","title":"강릉 경포벚꽃축제",
                "eventstartdate":"20260404","eventenddate":"20260411",
                "firstimage":"https://tong.visitkorea.or.kr/cms/resource/23/4041323_image2_1.jpg",
                "mapx":"128.895500767487","mapy":"37.7952793467",
                "modifiedtime":"20260224132726",
                "lDongRegnCd":"51","lDongSignguCd":"150",
                "lclsSystm1":"EV","lclsSystm2":"EV01","lclsSystm3":"EV010200"
              }]},"numOfRows":1,"pageNo":1,"totalCount":54}}}
            """;

    @Test
    @DisplayName("축제 응답을 파싱하면 행사 기간이 채워진다")
    void parseFestival() {
        TourApiResponse<FestivalItem> response =
                mapper.readValue(FESTIVAL_JSON, new TypeReference<TourApiResponse<FestivalItem>>() {});

        FestivalItem item = response.items().getFirst();
        assertThat(item.sigunguCd()).isEqualTo(51150);
        assertThat(item.eventstartdate()).isEqualTo("20260404");
        assertThat(item.eventenddate()).isEqualTo("20260411");
        assertThat(item.mapx()).isEqualTo("128.895500767487");
    }

    @Test
    @DisplayName("결과 0건이면 items가 객체 대신 빈 문자열로 온다 (client가 catch로 방어하는 근거)")
    void emptyItemsComesAsBlankString() {
        String emptyJson = """
                {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},
                 "body":{"items":"","numOfRows":0,"pageNo":1,"totalCount":0}}}
                """;

        assertThatThrownBy(() ->
                mapper.readValue(emptyJson, new TypeReference<TourApiResponse<SpotItem>>() {})
        ).isInstanceOf(JacksonException.class);
    }
}
