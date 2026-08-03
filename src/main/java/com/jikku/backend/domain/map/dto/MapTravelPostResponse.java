package com.jikku.backend.domain.map.dto;

import lombok.Builder;

@Builder
public record MapTravelPostResponse(
  Long mapStickerId,
  String stickerType,
  Long travelPostId,
  String firstImage,
  String title,
  Float posX,
  Float posY,
  Float scale,
  Integer zIndex
) {
}
