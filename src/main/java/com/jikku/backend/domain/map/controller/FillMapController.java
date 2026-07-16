package com.jikku.backend.domain.map.controller;

import com.jikku.backend.domain.map.dto.FillMapRequest;
import com.jikku.backend.domain.map.dto.FillMapResponse;
import com.jikku.backend.domain.map.service.FillMapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.jikku.backend.global.apiPayload.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map-design")
public class FillMapController {

  private final FillMapService fillMapService;

  @GetMapping
  public ApiResponse<List<FillMapResponse>> getSigunguFillMap(@AuthenticationPrincipal Long memberId) {
    return ApiResponse.onSuccess(fillMapService.getSigunguFillMap(memberId));
  }

  @PostMapping
  public ApiResponse<FillMapResponse> saveFillMap(@AuthenticationPrincipal Long memberId, @Valid @RequestBody FillMapRequest request) {
    return ApiResponse.onSuccess(fillMapService.saveFillMap(request, memberId));
  }

  @PatchMapping("/{fillMapId}")
  public ApiResponse<FillMapResponse> updateFillMap(
    @AuthenticationPrincipal Long memberId,
    @PathVariable Long fillMapId,
    @Valid @RequestBody FillMapRequest request
  ) {
    return ApiResponse.onSuccess(fillMapService.updateFillMap(fillMapId, request, memberId));
  }

  @GetMapping("/emd")
  public ApiResponse<List<FillMapResponse>> getEmdFillMap(
    @AuthenticationPrincipal Long memberId,
    @RequestParam("sigunguCd") Integer sigunguCd
  ) {
    return ApiResponse.onSuccess(fillMapService.getEmdFillMap(memberId, sigunguCd));
  }
}
