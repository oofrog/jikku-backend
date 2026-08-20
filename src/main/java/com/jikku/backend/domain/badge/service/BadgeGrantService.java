package com.jikku.backend.domain.badge.service;

import com.jikku.backend.domain.badge.entity.Badge;
import com.jikku.backend.domain.badge.enums.BadgeType;
import com.jikku.backend.domain.badge.enums.RegionBadge;
import com.jikku.backend.domain.badge.repository.BadgeRepository;
import com.jikku.backend.domain.member.entity.Member;
import com.jikku.backend.domain.member.repository.MemberRepository;
import com.jikku.backend.domain.mission.repository.MissionSpotRepository;
import com.jikku.backend.domain.member.exception.MemberErrorCode;
import com.jikku.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BadgeGrantService {

  private static final long REGION_BADGE_CONDITION_COUNT = 5L;

  private final BadgeRepository badgeRepository;
  private final MemberRepository memberRepository;
  private final MissionSpotRepository missionSpotRepository;

  public void grantRegionBadgeIfEligible(Long memberId, Integer sigunguCd) {
    long completedMissionCount =
      missionSpotRepository.countCompletedByMemberIdAndSigunguCd(memberId, sigunguCd);

    if (completedMissionCount < REGION_BADGE_CONDITION_COUNT) {
      return;
    }

    Optional<RegionBadge> regionBadge = RegionBadge.fromSigunguCd(sigunguCd);
    if (regionBadge.isEmpty()) {
      return;
    }

    String badgeNo = regionBadge.get().getBadgeNo();
    grantIfAbsent(memberId, BadgeType.REGION, badgeNo);
  }

  private void grantIfAbsent(Long memberId, BadgeType badgeType, String badgeNo) {
    if (badgeRepository.existsByMember_MemberIdAndBadgeNo(memberId, badgeNo)) {
      return;
    }

    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new BaseException(MemberErrorCode.MEMBER_NOT_FOUND));

    badgeRepository.save(Badge.of(member, badgeType, badgeNo));
  }
}
