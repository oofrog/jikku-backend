package com.jikku.backend.domain.travelPost.dto;

import com.jikku.backend.domain.travelPost.enums.BlockType;
import jakarta.validation.constraints.NotNull;

public record TravelPostBlockCreateRequest(
  @NotNull(message = "blockType은 필수입니다.")
  BlockType blockType,

  @NotNull(message = "sortOrder는 필수입니다.")
  Integer sortOrder,

  String textContent,
  String imgUrl
) {
}
