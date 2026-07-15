package com.jikku.backend.domain.map.controller;

import com.jikku.backend.domain.map.dto.FillMapRequest;
import com.jikku.backend.domain.map.dto.FillMapResponse;
import com.jikku.backend.domain.map.service.FillMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map-design")
public class FillMapController {

  private final FillMapService fillMapService;

  @GetMapping
  public ResponseEntity<List<FillMapResponse>> getSigunguFillMap() {
    List<FillMapResponse> response = fillMapService.getSigunguFillMap(1L);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<FillMapResponse> saveFillMap(@RequestBody FillMapRequest request) {
    FillMapResponse response = fillMapService.saveFillMap(request, 1L);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{fillMapId}")
  public ResponseEntity<FillMapResponse> updateFillMap(
    @PathVariable Long fillMapId,
    @RequestBody FillMapRequest request
  ) {
    FillMapResponse response = fillMapService.updateFillMap(fillMapId, request, 1L);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/emd")
  public ResponseEntity<List<FillMapResponse>> getEmdFillMap(@RequestParam Long sigunguId) {
    List<FillMapResponse> response = fillMapService.getEmdFillMap(1L, sigunguId);
    return ResponseEntity.ok(response);
  }
}
