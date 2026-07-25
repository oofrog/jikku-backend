package com.jikku.backend.domain.map.dto;

import com.jikku.backend.domain.map.entity.Sticker;
import lombok.Builder;

@Builder
public record StickerResponse(
  Long stickerId,
  String stickerUrl
) {
  public static StickerResponse from(Sticker sticker) {
    return StickerResponse.builder()
      .stickerId(sticker.getStickerId())
      .stickerUrl(sticker.getStickerUrl())
      .build();
  }
}
