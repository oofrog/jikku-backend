package com.jikku.backend.domain.badge.dto;

import java.util.List;

public record BadgeListResponse(
  List<BadgeResponse> content
) {
  public static BadgeListResponse from(List<BadgeResponse> content) {
    return new BadgeListResponse(content);
  }
}
