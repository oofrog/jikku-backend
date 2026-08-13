package com.jikku.backend.domain.spot.controller;

import com.jikku.backend.domain.spot.dto.ContentListResponse;
import com.jikku.backend.domain.spot.dto.FestivalDetailResponse;
import com.jikku.backend.domain.spot.dto.FestivalSummaryResponse;
import com.jikku.backend.domain.spot.service.FestivalService;
import com.jikku.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 축제 조회 API. 관광지와 대등한 리소스라 /spots 밑에 두지 않는다 (테이블도 contentId 체계도 별개).
 * 패키지는 관광지와 DTO·에러코드를 공유해 domain/spot에 함께 둔다.
 */
@Tag(name = "Festival", description = "축제 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/festivals")
public class FestivalController {

    private final FestivalService festivalService;

    @Operation(summary = "축제 목록 조회",
            description = "아직 끝나지 않은 축제를 시작일 오름차순으로 돌려준다. 지난 축제는 제외한다.")
    @GetMapping
    public ApiResponse<ContentListResponse<FestivalSummaryResponse>> getFestivals() {
        return ApiResponse.onSuccess(festivalService.getFestivals());
    }

    @Operation(summary = "축제 세부 조회", description = "contentId로 단건 조회한다. 없으면 FESTIVAL404_1.")
    @GetMapping("/{contentId}")
    public ApiResponse<FestivalDetailResponse> getFestival(
            @Parameter(description = "TourAPI 콘텐츠 ID", example = "232325")
            @PathVariable Long contentId) {
        return ApiResponse.onSuccess(festivalService.getFestival(contentId));
    }
}
