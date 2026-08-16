package com.jikku.backend.domain.mission.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record MissionVerifyRequest(
  @NotNull
  @DecimalMin(value = "124.0")
  @DecimalMax(value = "132.0")
  Double userX,

  @NotNull
  @DecimalMin(value = "33.0")
  @DecimalMax(value = "39.0")
  Double userY
) {
}
