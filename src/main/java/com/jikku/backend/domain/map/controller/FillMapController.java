package com.jikku.backend.domain.map.controller;

import com.jikku.backend.domain.map.dto.FillMapRequest;
import com.jikku.backend.domain.map.dto.FillMapListResponse;
import com.jikku.backend.domain.map.dto.FillMapResponse;
import com.jikku.backend.domain.map.service.FillMapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.jikku.backend.global.apiPayload.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map-design")
public class FillMapController {

  private final FillMapService fillMapService;

  @GetMapping
  public ApiResponse<FillMapListResponse> getSigunguFillMap(@AuthenticationPrincipal Long memberId) {
    return ApiResponse.onSuccess(FillMapListResponse.from(fillMapService.getSigunguFillMap(memberId)));
  }

  @PostMapping
  public ApiResponse<FillMapResponse> saveFillMap(@AuthenticationPrincipal Long memberId, @Valid @RequestBody FillMapRequest request) {
    return ApiResponse.onSuccess(fillMapService.saveFillMap(request, memberId));
  }

  @PatchMapping("/update/{fillMapId}")
  public ApiResponse<FillMapResponse> updateFillMap(
    @AuthenticationPrincipal Long memberId,
    @PathVariable Long fillMapId,
    @Valid @RequestBody FillMapRequest request
  ) {
    return ApiResponse.onSuccess(fillMapService.updateFillMap(fillMapId, request, memberId));
  }

  @GetMapping("/{sigunguCd}")
  public ApiResponse<FillMapListResponse> getEmdFillMap(
    @AuthenticationPrincipal Long memberId,
    @PathVariable Integer sigunguCd
  ) {
    return ApiResponse.onSuccess(FillMapListResponse.from(fillMapService.getEmdFillMap(memberId, sigunguCd)));
  }
}
