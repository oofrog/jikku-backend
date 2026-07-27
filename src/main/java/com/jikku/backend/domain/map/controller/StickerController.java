package com.jikku.backend.domain.map.controller;

import com.jikku.backend.domain.map.dto.FillMapListResponse;
import com.jikku.backend.domain.map.dto.StickerResponse;
import com.jikku.backend.domain.map.service.StickerService;
import com.jikku.backend.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stickers")
public class StickerController {

  private final StickerService stickerService;

  @GetMapping
  public ApiResponse<FillMapListResponse<StickerResponse>> getStickers() {
    return ApiResponse.onSuccess(stickerService.getStickers());
  }
}
