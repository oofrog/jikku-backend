package com.jikku.backend.domain.region.controller;

import com.jikku.backend.domain.region.dto.UnderservedRegionListResponse;
import com.jikku.backend.domain.region.service.UnderservedRegionService;
import com.jikku.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Region", description = "지역 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/regions")
public class RegionController {

    private final UnderservedRegionService underservedRegionService;

    @Operation(summary = "관광소외지역 조회",
            description = "이번 달 방문자가 적은 하위 6개 시군구를 rankAsc 오름차순으로 돌려준다. "
                    + "기준 월은 서버 시각으로 정하며 응답의 month에 담긴다.")
    @GetMapping("/underserved")
    public ApiResponse<UnderservedRegionListResponse> getUnderservedRegions() {
        return ApiResponse.onSuccess(underservedRegionService.getUnderservedRegions());
    }
}
