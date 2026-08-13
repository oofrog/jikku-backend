package com.jikku.backend.domain.spot.controller;

import com.jikku.backend.domain.spot.dto.ContentListResponse;
import com.jikku.backend.domain.spot.dto.SpotDetailResponse;
import com.jikku.backend.domain.spot.dto.SpotSummaryResponse;
import com.jikku.backend.domain.spot.service.SpotService;
import com.jikku.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Spot", description = "관광지 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/spots")
public class SpotController {

    private final SpotService spotService;

    @Operation(summary = "오늘의 관광지 추천",
            description = "강원 전체에서 무작위 10개. 날짜를 시드로 써서 같은 날에는 결과가 같다. "
                    + "대표 이미지가 없는 관광지는 후보에서 제외한다.")
    @GetMapping("/today")
    public ApiResponse<ContentListResponse<SpotSummaryResponse>> getTodaySpots() {
        return ApiResponse.onSuccess(spotService.getTodaySpots());
    }

    @Operation(summary = "관광지 세부 조회", description = "contentId로 단건 조회한다. 없으면 SPOT404_1.")
    @GetMapping("/{contentId}")
    public ApiResponse<SpotDetailResponse> getSpot(
            @Parameter(description = "TourAPI 콘텐츠 ID", example = "2761729")
            @PathVariable Long contentId) {
        return ApiResponse.onSuccess(spotService.getSpot(contentId));
    }
}
