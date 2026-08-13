package com.jikku.backend.domain.travelPost.dto;

import com.jikku.backend.domain.travelPost.entity.TravelPost;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public record TravelPostDetailResponse(
  Long travelPostId,
  String emdNm,
  LocalDate logDate,
  String title,
  List<TravelPostBlockResponse> blocks
) {
  public static TravelPostDetailResponse from(TravelPost travelPost) {
    return new TravelPostDetailResponse(
      travelPost.getTravelPostId(),
      travelPost.getEmd().getEmdNm(),
      travelPost.getLogDate(),
      travelPost.getTitle(),
      travelPost.getBlocks().stream()
        .sorted(Comparator.comparing(block -> block.getSortOrder()))
        .map(TravelPostBlockResponse::from)
        .toList()
    );
  }
}
