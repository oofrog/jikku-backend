package com.jikku.backend.domain.mission.dto;

public record MissionVerifyResponse(
  Long missionSpotId,
  Boolean isCompleted
) {
  public static MissionVerifyResponse of(Long missionSpotId, Boolean isCompleted) {
    return new MissionVerifyResponse(missionSpotId, isCompleted);
  }
}
