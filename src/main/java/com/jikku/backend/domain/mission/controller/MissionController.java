package com.jikku.backend.domain.mission.controller;

import com.jikku.backend.domain.mission.dto.MissionVerifyRequest;
import com.jikku.backend.domain.mission.dto.MissionVerifyResponse;
import com.jikku.backend.domain.mission.service.MissionService;
import com.jikku.backend.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/missions")
public class MissionController {

  private final MissionService missionService;

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
