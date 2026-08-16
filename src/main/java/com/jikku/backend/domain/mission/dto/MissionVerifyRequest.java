package com.jikku.backend.domain.mission.dto;

import jakarta.validation.constraints.NotNull;

public record MissionVerifyRequest(
  @NotNull Double userX,
  @NotNull Double userY
) {
}
