package com.jikku.backend.domain.spot.dto;

import com.jikku.backend.domain.spot.entity.Spot;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * 관광지 세부. firstImage는 null일 수 있어(1,356건 중 164건) 프론트가 기본 이미지로 대체한다.
 * mapX는 경도, mapY는 위도다(TourAPI 명명을 그대로 따름).
 */
@Builder
public record SpotDetailResponse(
        Long contentId,
        String title,
        String firstImage,
        String overview,
        String addr1,
        Integer sigunguCd,
        String sigunguNm,
        BigDecimal mapX,
        BigDecimal mapY
) {

    public static SpotDetailResponse from(Spot spot) {
        return SpotDetailResponse.builder()
                .contentId(spot.getContentId())
                .title(spot.getTitle())
                .firstImage(spot.getFirstImage())
                .overview(spot.getOverview())
                .addr1(spot.getAddr1())
                .sigunguCd(spot.getSigungu().getSigunguCd())
                .sigunguNm(spot.getSigungu().getSigunguNm())
                .mapX(spot.getMapX())
                .mapY(spot.getMapY())
                .build();
    }
}
