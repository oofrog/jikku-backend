package com.jikku.backend.domain.region.controller;

import com.jikku.backend.domain.region.dto.EmdListResponse;
import com.jikku.backend.domain.region.service.EmdService;
import com.jikku.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Emd", description = "읍면동 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/emds")
public class EmdController {

  private final EmdService emdService;

  @Operation(summary = "시군구별 읍면동 목록 조회")
  @GetMapping("/{sigunguCd}")
  public ApiResponse<EmdListResponse> getEmds(
    @PathVariable Integer sigunguCd
  ) {
    return ApiResponse.onSuccess(emdService.getEmds(sigunguCd));
  }
}
