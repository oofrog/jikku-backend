package com.jikku.backend.domain.mission.service;

import com.jikku.backend.domain.mission.dto.MissionVerifyRequest;
import com.jikku.backend.domain.mission.dto.MissionVerifyResponse;
import com.jikku.backend.domain.mission.entity.MissionSpot;
import com.jikku.backend.domain.mission.exception.MissionErrorCode;
import com.jikku.backend.domain.mission.repository.MissionSpotRepository;
import com.jikku.backend.domain.spot.entity.Spot;
import com.jikku.backend.domain.spot.repository.SpotRepository;
import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
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

  @Transactional
  public MissionVerifyResponse verifyMission(
    Long memberId,
    Long missionSpotId,
    MissionVerifyRequest request
  ) {
    MissionSpot missionSpot = missionSpotRepository.findByMissionSpotIdAndMemberId(missionSpotId, memberId)
            .orElseThrow(() -> new BaseException(
                    GeneralErrorCode.ENTITY_NOT_FOUND,
                    "존재하지 않는 미션 관광지입니다."
            ));

    if (Boolean.TRUE.equals(missionSpot.getIsCompleted())) {
      throw new BaseException(
              GeneralErrorCode.DUPLICATE_RESOURCE,
              "이미 방문 인증한 미션입니다."
      );
    }

    Long contentId = missionSpot.getContentId();

    Spot spot = spotRepository.findById(contentId)
            .orElseThrow(() -> new BaseException(
                    GeneralErrorCode.ENTITY_NOT_FOUND,
                    "관광지 정보를 찾을 수 없습니다."
            ));

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
