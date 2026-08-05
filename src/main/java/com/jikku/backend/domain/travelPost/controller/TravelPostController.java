package com.jikku.backend.domain.travelPost.controller;

import com.jikku.backend.domain.travelPost.dto.TravelPostDetailResponse;
import com.jikku.backend.domain.travelPost.service.TravelPostService;
import com.jikku.backend.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/travel-posts")
public class TravelPostController {

  private final TravelPostService travelPostService;

  @GetMapping("/detail/{travelPostId}")
  public ApiResponse<TravelPostDetailResponse> getTravelPostDetail(
    @PathVariable Long travelPostId
  ) {
    return ApiResponse.onSuccess(travelPostService.getTravelPostDetail(travelPostId));
  }
}
