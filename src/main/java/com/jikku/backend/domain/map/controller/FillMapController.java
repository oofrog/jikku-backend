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

  // 시군구 색칠 지도 조회
  @GetMapping
  public ResponseEntity<List<FillMapResponse>> getSigunguFillMap() {
    List<FillMapResponse> response = fillMapService.getSigunguFillMap(1L);
    return ResponseEntity.ok(response);
  }

  // 시군구 채우기
  @PostMapping
  public ResponseEntity<FillMapResponse> saveSigunguFillMap(@RequestBody FillMapRequest request) {
    FillMapResponse response = fillMapService.saveFillMap(request, 1L);
    return ResponseEntity.ok(response);
  }
}
