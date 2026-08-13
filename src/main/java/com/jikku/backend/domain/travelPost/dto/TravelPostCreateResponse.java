package com.jikku.backend.domain.travelPost.dto;

import com.jikku.backend.domain.travelPost.entity.TravelPost;

public record TravelPostCreateResponse(
  Long travelPostId,
  String title,
  String firstImage
) {
  public static TravelPostCreateResponse of(TravelPost travelPost) {
    return new TravelPostCreateResponse(
      travelPost.getTravelPostId(),
      travelPost.getTitle(),
      travelPost.getFirstImage()
    );
  }
}
