package com.jikku.backend.domain.region.dto;

import com.jikku.backend.domain.region.entity.RegionVisitorSummary;
import lombok.Builder;

/** 관광소외지역 카드. rankAsc는 그 달의 방문자 오름차순 순위(1 = 가장 적음). */
@Builder
public record UnderservedRegionResponse(
        Integer sigunguCd,
        String sigunguNm,
        Integer rankAsc
) {

    public static UnderservedRegionResponse from(RegionVisitorSummary summary) {
        return UnderservedRegionResponse.builder()
                .sigunguCd(summary.getSigunguCd())
                .sigunguNm(summary.getSigungu().getSigunguNm())
                .rankAsc(summary.getRankAsc())
                .build();
    }
}
