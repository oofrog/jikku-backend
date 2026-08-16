package com.jikku.backend.domain.spot.dto;

import com.jikku.backend.domain.spot.entity.Festival;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

/** 축제 세부. mapX는 경도, mapY는 위도다(TourAPI 명명을 그대로 따름). */
@Builder
public record FestivalDetailResponse(
        Long contentId,
        String title,
        String firstImage,
        String overview,
        String addr1,
        LocalDate eventStartDate,
        LocalDate eventEndDate,
        Integer sigunguCd,
        String sigunguNm,
        BigDecimal mapX,
        BigDecimal mapY
) {

    public static FestivalDetailResponse from(Festival festival) {
        return FestivalDetailResponse.builder()
                .contentId(festival.getContentId())
                .title(festival.getTitle())
                .firstImage(festival.getFirstImage())
                .overview(festival.getOverview())
                .addr1(festival.getAddr1())
                .eventStartDate(festival.getEventStartDate())
                .eventEndDate(festival.getEventEndDate())
                .sigunguCd(festival.getSigungu().getSigunguCd())
                .sigunguNm(festival.getSigungu().getSigunguNm())
                .mapX(festival.getMapX())
                .mapY(festival.getMapY())
                .build();
    }
}
