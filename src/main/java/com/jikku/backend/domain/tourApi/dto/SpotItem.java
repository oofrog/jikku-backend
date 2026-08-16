package com.jikku.backend.domain.tourApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// /KorService2/areaBasedList2 응답 (contentTypeId=12 관광지)
@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotItem(
        String contentid,
        String title,
        String addr1,
        String mapx,
        String mapy,
        String firstimage,
        // 소문자 l + 대문자 D 조합이라 record 컴포넌트명 추론에 기대지 않고 키를 명시한다
        @JsonProperty("lDongRegnCd") String lDongRegnCd,
        @JsonProperty("lDongSignguCd") String lDongSignguCd,
        String lclsSystm1,
        String lclsSystm2,
        String lclsSystm3,
        String modifiedtime
) implements HasLDongCode {
}
