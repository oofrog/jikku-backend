package com.jikku.backend.domain.mission.service;

import com.jikku.backend.domain.badge.service.BadgeGrantService;
import com.jikku.backend.domain.mission.dto.MissionVerifyRequest;
import com.jikku.backend.domain.mission.dto.MissionVerifyResponse;
import com.jikku.backend.domain.mission.entity.MissionSpot;
import com.jikku.backend.domain.mission.exception.MissionErrorCode;
import com.jikku.backend.domain.mission.repository.MissionSpotRepository;
import com.jikku.backend.domain.spot.entity.Spot;
import com.jikku.backend.domain.spot.repository.SpotRepository;
import com.jikku.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MissionService {

  private static final double ALLOWED_DISTANCE_METERS = 1000.0;

  private final MissionSpotRepository missionSpotRepository;
  private final SpotRepository spotRepository;
  private final BadgeGrantService badgeGrantService;

  @Transactional
  public MissionVerifyResponse verifyMission(
    Long memberId,
    Long missionSpotId,
    MissionVerifyRequest request
  ) {
    MissionSpot missionSpot = missionSpotRepository.findByMissionSpotIdAndMemberId(missionSpotId, memberId)
      .orElseThrow(() -> new BaseException(MissionErrorCode.MISSION_SPOT_NOT_FOUND));

    if (Boolean.TRUE.equals(missionSpot.getIsCompleted())) {
      throw new BaseException(MissionErrorCode.MISSION_ALREADY_COMPLETED);
    }

    Long contentId = missionSpot.getContentId();

    Spot spot = spotRepository.findById(contentId)
      .orElseThrow(() -> new BaseException(MissionErrorCode.MISSION_TARGET_SPOT_NOT_FOUND));

    if (spot.getMapX() == null || spot.getMapY() == null) {
      throw new BaseException(MissionErrorCode.MISSION_COORDINATE_NOT_FOUND);
    }

    double distance = calculateDistance(
      request.userY(),
      request.userX(),
      spot.getMapY().doubleValue(),
      spot.getMapX().doubleValue()
    );

    if (distance > ALLOWED_DISTANCE_METERS) {
      throw new BaseException(MissionErrorCode.MISSION_OUT_OF_RANGE);
    }

    missionSpot.complete();
    badgeGrantService.grantRegionBadgeIfEligible(memberId, missionSpot.getSigunguCd());

    return MissionVerifyResponse.of(
      missionSpot.getMissionSpotId(),
      missionSpot.getIsCompleted()
    );
  }

  private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    double earthRadius = 6371000;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);

    double a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
          Math.sin(dLon / 2) * Math.sin(dLon / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return earthRadius * c;
  }
}
