package com.jikku.backend.domain.spot.dto;

import com.jikku.backend.domain.spot.entity.Spot;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * 오늘의 추천 카드. 지도에 찍을 좌표와 설명까지 함께 내려 카드에서 바로 보여줄 수 있게 한다.
 * 후보를 firstImage 있는 것으로 한정하므로 이 응답의 firstImage는 항상 존재한다.
 */
@Builder
public record SpotSummaryResponse(
        Long contentId,
        String title,
        String firstImage,
        String overview,
        Integer sigunguCd,
        String sigunguNm,
        BigDecimal mapX,
        BigDecimal mapY
) {

    public static SpotSummaryResponse from(Spot spot) {
        return SpotSummaryResponse.builder()
                .contentId(spot.getContentId())
                .title(spot.getTitle())
                .firstImage(spot.getFirstImage())
                .overview(spot.getOverview())
                .sigunguCd(spot.getSigungu().getSigunguCd())
                .sigunguNm(spot.getSigungu().getSigunguNm())
                .mapX(spot.getMapX())
                .mapY(spot.getMapY())
                .build();
    }
}
