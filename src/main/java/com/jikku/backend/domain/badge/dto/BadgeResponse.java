package com.jikku.backend.domain.badge.dto;

import com.jikku.backend.domain.badge.entity.Badge;

public record BadgeResponse(
  Long badgeId,
  String badgeType,
  String badgeNo
) {
  public static BadgeResponse of(Badge badge) {
    return new BadgeResponse(
      badge.getBadgeId(),
      badge.getBadgeType().name(),
      badge.getBadgeNo()
    );
  }
}
