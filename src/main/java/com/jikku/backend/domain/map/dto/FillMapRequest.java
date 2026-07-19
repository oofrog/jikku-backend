package com.jikku.backend.domain.map.dto;

import com.jikku.backend.domain.map.enums.FillType;
import com.jikku.backend.domain.map.enums.MapType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record FillMapRequest(
  @NotNull(message = "sigunguCd는 필수입니다.")
  @Schema(example = "51110")
  Integer sigunguCd,

  @NotNull(message = "fillType은 필수입니다.")
  @Schema(example = "COLOR", allowableValues = {"COLOR", "IMAGE"})
  FillType fillType,

  @Schema(example = "#4F46E5", nullable = true)
  String color,

  @Schema(example = "https://example.com/map.png", nullable = true)
  String imgUrl
) {}
