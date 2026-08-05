package com.jikku.backend.domain.travelPost.controller;

import com.jikku.backend.domain.travelPost.dto.TravelPostDetailResponse;
import com.jikku.backend.domain.travelPost.service.TravelPostService;
import com.jikku.backend.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jikku.backend.domain.map.dto.FillMapListResponse;
import com.jikku.backend.domain.travelPost.dto.TravelPostResponse;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

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

  @GetMapping("/{sigunguCd}")
  public ApiResponse<FillMapListResponse<TravelPostResponse>> getTravelPosts(
    @PathVariable Integer sigunguCd,
    @RequestParam(required = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    return ApiResponse.onSuccess(travelPostService.getTravelPosts(sigunguCd, date));
  }
}
