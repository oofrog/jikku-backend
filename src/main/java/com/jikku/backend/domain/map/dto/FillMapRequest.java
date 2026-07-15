package com.jikku.backend.domain.map.dto;

import com.jikku.backend.domain.map.enums.FillType;
import com.jikku.backend.domain.map.enums.MapType;
import io.swagger.v3.oas.annotations.media.Schema;

public record FillMapRequest(
  @Schema(example = "11010")
  Long sigunguId,

  @Schema(example = "11010101", nullable = true)
  Long emdId,

  @Schema(example = "SIGUNGU", allowableValues = {"SIGUNGU", "EMD"})
  MapType mapType,

  @Schema(example = "COLOR", allowableValues = {"COLOR", "IMAGE"})
  FillType fillType,

  @Schema(example = "#4F46E5", nullable = true)
  String color,

  @Schema(example = "https://example.com/map.png", nullable = true)
  String imgUrl
) {}
