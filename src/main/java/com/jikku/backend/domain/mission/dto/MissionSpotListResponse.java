package com.jikku.backend.domain.mission.dto;

import java.util.List;

/**
 * content에는 아직 인증하지 않은 미션만 담긴다.
 * 따라서 그 시군구의 전체 미션 수는 completedCount + content.length이고, 진행률도 이 분모로 계산한다.
 */
public record MissionSpotListResponse(
  long completedCount,
  List<MissionSpotResponse> content
) {

  public static MissionSpotListResponse of(long completedCount, List<MissionSpotResponse> content) {
    return new MissionSpotListResponse(completedCount, content);
  }
}
