package com.jikku.backend.domain.mission.dto;

import com.jikku.backend.domain.mission.entity.MissionSpot;
import com.jikku.backend.domain.spot.entity.Spot;
import java.math.BigDecimal;
import lombok.Builder;

/**
 * 미션 카드 한 장. 방문 인증(PATCH /missions/verify/{missionSpotId})에 쓸 missionSpotId를 함께 내린다.
 * 후보를 대표 이미지가 있는 관광지로 한정하므로 firstImage는 실제로는 항상 채워진다.
 * 목록에는 아직 인증하지 않은 미션만 담기므로 isCompleted는 늘 false다(명세 호환을 위해 남겨 둔 필드).
 */
@Builder
public record MissionSpotResponse(
  Long missionSpotId,
  Boolean isCompleted,
  Long contentId,
  String title,
  String firstImage,
  String overview,
  BigDecimal mapX,
  BigDecimal mapY
) {

  private static final int OVERVIEW_MAX_LENGTH = 30;

  public static MissionSpotResponse of(MissionSpot missionSpot, Spot spot) {
    return MissionSpotResponse.builder()
      .missionSpotId(missionSpot.getMissionSpotId())
      .isCompleted(missionSpot.getIsCompleted())
      .contentId(spot.getContentId())
      .title(spot.getTitle())
      .firstImage(spot.getFirstImage())
      .overview(summarize(spot.getOverview()))
      .mapX(spot.getMapX())
      .mapY(spot.getMapY())
      .build();
  }

  /**
   * 카드용 한 줄 소개. 원문은 TourAPI 상세 설명이라 길고 &lt;br&gt; 같은 태그가 섞여 있어,
   * 그냥 30자를 자르면 태그가 중간에서 끊긴다. 태그를 걷어내고 공백을 정리한 뒤 자른다.
   */
  private static String summarize(String overview) {
    if (overview == null) {
      return null;
    }

    String plain = overview.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();

    return plain.length() <= OVERVIEW_MAX_LENGTH
      ? plain
      : plain.substring(0, OVERVIEW_MAX_LENGTH) + "…";
  }
}
