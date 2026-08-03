package com.jikku.backend.domain.map.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record MapTravelPostRequest(
  @NotNull(message = "travelPostId는 필수입니다.")
  Long travelPostId,

  @NotNull(message = "posX는 필수입니다.")
  @DecimalMin(value = "0.0", message = "posX는 0 이상이어야 합니다.")
  @DecimalMax(value = "1.0", message = "posX는 1 이하여야 합니다.")
  Float posX,

  @NotNull(message = "posY는 필수입니다.")
  @DecimalMin(value = "0.0", message = "posY는 0 이상이어야 합니다.")
  @DecimalMax(value = "1.0", message = "posY는 1 이하여야 합니다.")
  Float posY,

  @NotNull(message = "scale은 필수입니다.")
  @Positive(message = "scale은 0보다 커야 합니다.")
  Float scale,

  @NotNull(message = "zIndex는 필수입니다.")
  @PositiveOrZero(message = "zIndex는 0 이상이어야 합니다.")
  Integer zIndex
) {
}
