package com.jikku.backend.domain.map.controller;

import com.jikku.backend.domain.map.dto.FillMapRequest;
import com.jikku.backend.domain.map.dto.FillMapListResponse;
import com.jikku.backend.domain.map.dto.FillMapResponse;
import com.jikku.backend.domain.map.service.EmdFillService;
import com.jikku.backend.domain.map.service.SigunguFillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.jikku.backend.global.apiPayload.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map-design")
public class FillMapController {

  private final SigunguFillService sigunguFillService;
  private final EmdFillService emdFillService;

  @GetMapping
  public ApiResponse<FillMapListResponse> getSigunguFillMap(@AuthenticationPrincipal Long memberId) {
    return ApiResponse.onSuccess(FillMapListResponse.from(sigunguFillService.getSigunguFillMap(memberId)));
  }

  @PostMapping
  public ApiResponse<FillMapResponse> saveFillMap(@AuthenticationPrincipal Long memberId, @Valid @RequestBody FillMapRequest request) {
    return ApiResponse.onSuccess(sigunguFillService.saveFillMap(request, memberId));
  }

  @PatchMapping("/update/{fillMapId}")
  public ApiResponse<FillMapResponse> updateFillMap(
    @AuthenticationPrincipal Long memberId,
    @PathVariable Long fillMapId,
    @Valid @RequestBody FillMapRequest request
  ) {
    return ApiResponse.onSuccess(sigunguFillService.updateFillMap(fillMapId, request, memberId));
  }

  @GetMapping("/{sigunguCd}")
  public ApiResponse<FillMapListResponse> getEmdFillMap(
    @AuthenticationPrincipal Long memberId,
    @PathVariable Integer sigunguCd
  ) {
    return ApiResponse.onSuccess(FillMapListResponse.from(emdFillService.getEmdFillMap(memberId, sigunguCd)));
  }
}
