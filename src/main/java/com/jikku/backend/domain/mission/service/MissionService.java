package com.jikku.backend.domain.mission.service;

import com.jikku.backend.domain.badge.service.BadgeGrantService;
import com.jikku.backend.domain.mission.dto.MissionSpotDetailResponse;
import com.jikku.backend.domain.mission.dto.MissionSpotListResponse;
import com.jikku.backend.domain.mission.dto.MissionSpotResponse;
import com.jikku.backend.domain.mission.dto.MissionVerifyRequest;
import com.jikku.backend.domain.mission.dto.MissionVerifyResponse;
import com.jikku.backend.domain.mission.entity.MissionSpot;
import com.jikku.backend.domain.mission.exception.MissionErrorCode;
import com.jikku.backend.domain.mission.repository.MissionSpotRepository;
import com.jikku.backend.domain.region.repository.SigunguRepository;
import com.jikku.backend.domain.spot.entity.Spot;
import com.jikku.backend.domain.spot.repository.SpotRepository;
import com.jikku.backend.global.exception.BaseException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MissionService {

  private static final double ALLOWED_DISTANCE_METERS = 1000.0;

  private final MissionSpotRepository missionSpotRepository;
  private final SpotRepository spotRepository;
  private final SigunguRepository sigunguRepository;
  private final MissionAssignService missionAssignService;
  private final BadgeGrantService badgeGrantService;

  /**
   * 시군구별 미션 목록. 이미 인증한 미션은 빼고 남은 것만 내려준다.
   * 그 회원이 이 시군구를 처음 열면 이 조회가 적재를 겸한다.
   * 트랜잭션을 열지 않는 이유는 적재 실패(동시 요청) 후 재조회를 같은 트랜잭션에서 할 수 없어서다.
   */
  public MissionSpotListResponse getMissions(Long memberId, Integer sigunguCd) {
    if (!sigunguRepository.existsById(sigunguCd)) {
      throw new BaseException(MissionErrorCode.MISSION_SIGUNGU_NOT_FOUND);
    }

    // 남은 미션 목록이 아니라 존재 여부로 판정한다. 목록으로 보면 20개를 다 인증한 회원에게 미션이 새로 적재된다.
    if (!missionSpotRepository.existsByMemberIdAndSigunguCd(memberId, sigunguCd)) {
      assign(memberId, sigunguCd);
    }

    List<MissionSpot> missionSpots = missionSpotRepository
      .findByMemberIdAndSigunguCdAndIsCompletedFalseOrderByMissionSpotId(memberId, sigunguCd);

    Map<Long, Spot> spotsById = spotRepository
      .findAllById(missionSpots.stream().map(MissionSpot::getContentId).toList())
      .stream()
      .collect(Collectors.toMap(Spot::getContentId, Function.identity()));

    List<MissionSpotResponse> content = missionSpots.stream()
      .filter(missionSpot -> spotsById.containsKey(missionSpot.getContentId()))
      .map(missionSpot -> MissionSpotResponse.of(missionSpot, spotsById.get(missionSpot.getContentId())))
      .toList();

    return MissionSpotListResponse.of(
      missionSpotRepository.countCompletedByMemberIdAndSigunguCd(memberId, sigunguCd),
      content
    );
  }

  private void assign(Long memberId, Integer sigunguCd) {
    try {
      missionAssignService.assign(memberId, sigunguCd);
    } catch (DataIntegrityViolationException e) {
      // (member_id, content_id) 유니크에 걸렸다 = 동시 요청이 먼저 적재했다. 그쪽이 넣은 20개를 아래에서 그대로 읽는다.
    }
  }

  /**
   * 미션 관광지 세부. 조회 키를 contentId가 아니라 missionSpotId로 둔 이유는
   * 남의 미션을 못 보게 하면서 인증 상태(isCompleted)까지 같이 내리기 위해서다.
   */
  @Transactional(readOnly = true)
  public MissionSpotDetailResponse getMissionSpot(Long memberId, Long missionSpotId) {
    MissionSpot missionSpot = missionSpotRepository.findByMissionSpotIdAndMemberId(missionSpotId, memberId)
      .orElseThrow(() -> new BaseException(MissionErrorCode.MISSION_SPOT_NOT_FOUND));

    Spot spot = spotRepository.findWithSigungu(missionSpot.getContentId())
      .orElseThrow(() -> new BaseException(MissionErrorCode.MISSION_TARGET_SPOT_NOT_FOUND));

    return MissionSpotDetailResponse.of(missionSpot, spot);
  }

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
