package com.jikku.backend.domain.tourApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// /KorService2/searchFestival2 응답
// 축제 기간(eventstartdate/enddate)은 이 오퍼레이션에만 있어 areaBasedList2로는 대체 불가
@JsonIgnoreProperties(ignoreUnknown = true)
public record FestivalItem(
        String contentid,
        String title,
        String addr1,
        String mapx,
        String mapy,
        String firstimage,
        @JsonProperty("lDongRegnCd") String lDongRegnCd,
        @JsonProperty("lDongSignguCd") String lDongSignguCd,
        String lclsSystm1,
        String lclsSystm2,
        String lclsSystm3,
        String eventstartdate,
        String eventenddate,
        String modifiedtime
) {

    public int sigunguCd() {
        return Integer.parseInt(lDongRegnCd + lDongSignguCd);
    }
}
