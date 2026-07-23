package com.jikku.backend.domain.map.dto;

import com.jikku.backend.domain.map.enums.FillType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SigunguFillRequest(
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
) {
  @AssertTrue(message = "fillType이 COLOR면 color는 필수이고 imgUrl은 비어 있어야 하며, IMAGE면 imgUrl은 필수이고 color는 비어 있어야 합니다.")
  @Schema(hidden = true)
  public boolean isValidFillValue() {
    if (fillType == FillType.COLOR) {
      return hasText(color) && !hasText(imgUrl);
    }

    if (fillType == FillType.IMAGE) {
      return hasText(imgUrl) && !hasText(color);
    }

    return false;
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
