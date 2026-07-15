package com.jikku.backend.domain.map.controller;

import com.jikku.backend.domain.map.dto.FillMapRequest;
import com.jikku.backend.domain.map.dto.FillMapResponse;
import com.jikku.backend.domain.map.service.FillMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<ApiResponse<List<FillMapResponse>>> getSigunguFillMap(@AuthenticationPrincipal Long memberId) {
    List<FillMapResponse> response = fillMapService.getSigunguFillMap(memberId);
    return ResponseEntity.ok(ApiResponse.onSuccess(response));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<FillMapResponse>> saveFillMap(@AuthenticationPrincipal Long memberId, @RequestBody FillMapRequest request) {
    FillMapResponse response = fillMapService.saveFillMap(request, memberId);
    return ResponseEntity.ok(ApiResponse.onSuccess(response));
  }

  @PatchMapping("/{fillMapId}")
  public ResponseEntity<ApiResponse<FillMapResponse>> updateFillMap(
    @AuthenticationPrincipal Long memberId,
    @PathVariable Long fillMapId,
    @RequestBody FillMapRequest request
  ) {
    FillMapResponse response = fillMapService.updateFillMap(fillMapId, request, memberId);
    return ResponseEntity.ok(ApiResponse.onSuccess(response));
  }

  @GetMapping("/emd")
  public ResponseEntity<ApiResponse<List<FillMapResponse>>> getEmdFillMap(
    @AuthenticationPrincipal Long memberId,
    @RequestParam("sigunguCd") Integer sigunguCd
  ) {
    List<FillMapResponse> response = fillMapService.getEmdFillMap(memberId, sigunguCd);
    return ResponseEntity.ok(ApiResponse.onSuccess(response));
  }
}
