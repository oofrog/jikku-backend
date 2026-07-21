package com.jikku.backend.domain.map.controller;

import com.jikku.backend.domain.map.dto.SigunguFillRequest;
import com.jikku.backend.domain.map.dto.FillMapListResponse;
import com.jikku.backend.domain.map.dto.SigunguFillResponse;
import com.jikku.backend.domain.map.dto.EmdFillResponse;
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
  public ApiResponse<FillMapListResponse<SigunguFillResponse>> getSigunguFillMap(@AuthenticationPrincipal Long memberId) {
    return ApiResponse.onSuccess(sigunguFillService.getSigunguFillMap(memberId));
  }

  @PostMapping
  public ApiResponse<SigunguFillResponse> saveFillMap(@AuthenticationPrincipal Long memberId, @Valid @RequestBody SigunguFillRequest request) {
    return ApiResponse.onSuccess(sigunguFillService.saveFillMap(request, memberId));
  }

  @PatchMapping("/update/{fillMapId}")
  public ApiResponse<SigunguFillResponse> updateFillMap(
    @AuthenticationPrincipal Long memberId,
    @PathVariable Long fillMapId,
    @Valid @RequestBody SigunguFillRequest request
  ) {
    return ApiResponse.onSuccess(sigunguFillService.updateFillMap(fillMapId, request, memberId));
  }

  @GetMapping("/{sigunguCd}")
  public ApiResponse<FillMapListResponse<EmdFillResponse>> getEmdFillMap(
    @AuthenticationPrincipal Long memberId,
    @PathVariable Integer sigunguCd
  ) {
    return ApiResponse.onSuccess(emdFillService.getEmdFillMap(memberId, sigunguCd));
  }
}
