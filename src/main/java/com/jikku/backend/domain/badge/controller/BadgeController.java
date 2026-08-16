package com.jikku.backend.domain.badge.controller;

import com.jikku.backend.domain.badge.dto.BadgeListResponse;
import com.jikku.backend.domain.badge.service.BadgeService;
import com.jikku.backend.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/badges")
public class BadgeController {

  private final BadgeService badgeService;

  @GetMapping
  public ApiResponse<BadgeListResponse> getBadges(
    @AuthenticationPrincipal Long memberId
  ) {
    return ApiResponse.onSuccess(
      badgeService.getBadges(memberId)
    );
  }
}
