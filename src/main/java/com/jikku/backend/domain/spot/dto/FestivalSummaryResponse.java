package com.jikku.backend.domain.spot.dto;

import com.jikku.backend.domain.spot.entity.Festival;
import lombok.Builder;

import java.time.LocalDate;

/** 축제 목록 카드. 세부 조회에만 필요한 overview·주소·좌표는 빼서 목록 응답을 가볍게 유지한다. */
@Builder
public record FestivalSummaryResponse(
        Long contentId,
        String title,
        String firstImage,
        LocalDate eventStartDate,
        LocalDate eventEndDate,
        Integer sigunguCd,
        String sigunguNm
) {

    public static FestivalSummaryResponse from(Festival festival) {
        return FestivalSummaryResponse.builder()
                .contentId(festival.getContentId())
                .title(festival.getTitle())
                .firstImage(festival.getFirstImage())
                .eventStartDate(festival.getEventStartDate())
                .eventEndDate(festival.getEventEndDate())
                .sigunguCd(festival.getSigungu().getSigunguCd())
                .sigunguNm(festival.getSigungu().getSigunguNm())
                .build();
    }
}
