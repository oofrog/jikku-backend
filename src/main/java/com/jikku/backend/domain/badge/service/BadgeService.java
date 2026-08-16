package com.jikku.backend.domain.badge.service;

import com.jikku.backend.domain.badge.dto.BadgeListResponse;
import com.jikku.backend.domain.badge.dto.BadgeResponse;
import com.jikku.backend.domain.badge.entity.Badge;
import com.jikku.backend.domain.badge.repository.BadgeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BadgeService {

  private final BadgeRepository badgeRepository;

  @Transactional(readOnly = true)
  public BadgeListResponse getBadges(Long memberId) {
    List<Badge> badges = badgeRepository.findAllByMemberId(memberId);

    List<BadgeResponse> content = badges.stream()
      .map(BadgeResponse::of)
      .toList();

    return BadgeListResponse.from(content);
  }
}
