package com.jikku.backend.domain.map.dto;

import com.jikku.backend.domain.map.entity.MapSticker;
import lombok.Builder;

@Builder
public record MapStickerResponse(
  Long mapStickerId,
  String stickerType,
  Long travelPostId,
  Long stickerId,
  String stickerUrl,
  Float posX,
  Float posY,
  Float scale,
  Integer zIndex
) {
  public static MapStickerResponse from(MapSticker mapSticker) {
    return MapStickerResponse.builder()
      .mapStickerId(mapSticker.getMapStickerId())
      .stickerType(mapSticker.getStickerType().name())
      .travelPostId(mapSticker.getTravelPostId())
      .stickerId(mapSticker.getSticker() != null ? mapSticker.getSticker().getStickerId() : null)
      .stickerUrl(mapSticker.getSticker() != null ? mapSticker.getSticker().getStickerUrl() : null)
      .posX(mapSticker.getPosX())
      .posY(mapSticker.getPosY())
      .scale(mapSticker.getScale())
      .zIndex(mapSticker.getZIndex())
      .build();
  }
}
