package com.jikku.backend.domain.travelPost.dto;

import com.jikku.backend.domain.travelPost.entity.TravelPost;
import java.time.LocalDate;

public record TravelPostResponse(
  Long travelPostId,
  String firstImage,
  String emdNm,
  LocalDate logDate,
  String title
) {
  public static TravelPostResponse from(TravelPost travelPost) {
    return new TravelPostResponse(
      travelPost.getTravelPostId(),
      travelPost.getFirstImage(),
      travelPost.getEmd().getEmdNm(),
      travelPost.getLogDate(),
      travelPost.getTitle()
    );
  }
}
