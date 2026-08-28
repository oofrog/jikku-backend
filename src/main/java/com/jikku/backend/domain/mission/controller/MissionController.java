package com.jikku.backend.domain.mission.controller;

import com.jikku.backend.domain.mission.dto.MissionSpotListResponse;
import com.jikku.backend.domain.mission.dto.MissionVerifyRequest;
import com.jikku.backend.domain.mission.dto.MissionVerifyResponse;
import com.jikku.backend.domain.mission.service.MissionService;
import com.jikku.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Mission", description = "미션 관광지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/missions")
public class MissionController {

  private final MissionService missionService;

  @Operation(summary = "시군구별 미션 관광지 조회",
    description = "그 회원이 이 시군구를 처음 조회하면 대표 이미지가 있는 관광지 20개를 무작위로 뽑아 고정한다. "
      + "이후에는 언제 조회해도 같은 20개를 준다. 없는 시군구 코드면 MISSION404_4.")
  @GetMapping("/{sigunguCd}")
  public ApiResponse<MissionSpotListResponse> getMissions(
    @AuthenticationPrincipal Long memberId,
    @Parameter(description = "법정동 시군구 코드", example = "51110")
    @PathVariable Integer sigunguCd
  ) {
    return ApiResponse.onSuccess(missionService.getMissions(memberId, sigunguCd));
  }

  @PatchMapping("/verify/{missionSpotId}")
  public ApiResponse<MissionVerifyResponse> verifyMission(
    @AuthenticationPrincipal Long memberId,
    @PathVariable Long missionSpotId,
    @Valid @RequestBody MissionVerifyRequest request
  ) {
    return ApiResponse.onSuccess(
      missionService.verifyMission(memberId, missionSpotId, request)
    );
  }
}
