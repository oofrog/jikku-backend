package com.jikku.backend.domain.mission.dto;

import com.jikku.backend.domain.mission.entity.MissionSpot;
import com.jikku.backend.domain.spot.entity.Spot;
import java.math.BigDecimal;
import lombok.Builder;

/**
 * 미션 관광지 세부. 관광지 세부 조회와 같은 내용에 missionSpotId·isCompleted를 더한 것으로,
 * 이 화면에서 바로 방문 인증(PATCH /missions/verify/{missionSpotId})을 쏠 수 있게 하기 위함이다.
 * overview는 목록(30자 컷)과 달리 원문 전체를 내린다.
 */
@Builder
public record MissionSpotDetailResponse(
  Long missionSpotId,
  Boolean isCompleted,
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

  public static MissionSpotDetailResponse of(MissionSpot missionSpot, Spot spot) {
    return MissionSpotDetailResponse.builder()
      .missionSpotId(missionSpot.getMissionSpotId())
      .isCompleted(missionSpot.getIsCompleted())
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
