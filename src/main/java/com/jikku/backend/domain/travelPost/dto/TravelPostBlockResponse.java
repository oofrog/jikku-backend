package com.jikku.backend.domain.travelPost.dto;

import com.jikku.backend.domain.travelPost.entity.TravelPostBlock;

public record TravelPostBlockResponse(
  Long travelLogBlockId,
  String blockType,
  Integer sortOrder,
  String textContent,
  String imgUrl
) {
  public static TravelPostBlockResponse from(TravelPostBlock block) {
    return new TravelPostBlockResponse(
      block.getTravelPostBlockId(),
      block.getBlockType().name(),
      block.getSortOrder(),
      block.getText(),
      block.getImgUrl()
    );
  }
}
