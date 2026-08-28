package com.jikku.backend.domain.mission.dto;

import java.util.List;

/**
 * 전체 개수는 content 길이와 같아 따로 내리지 않는다. 진행률은 completedCount / content.length.
 */
public record MissionSpotListResponse(
  long completedCount,
  List<MissionSpotResponse> content
) {

  public static MissionSpotListResponse of(long completedCount, List<MissionSpotResponse> content) {
    return new MissionSpotListResponse(completedCount, content);
  }
}
