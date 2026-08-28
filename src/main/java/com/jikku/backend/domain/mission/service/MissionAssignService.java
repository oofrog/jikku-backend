package com.jikku.backend.domain.mission.service;

import com.jikku.backend.domain.mission.entity.MissionSpot;
import com.jikku.backend.domain.mission.repository.MissionSpotRepository;
import com.jikku.backend.domain.spot.repository.SpotRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 미션 적재만 담당한다. 조회(MissionService)와 빈을 나눈 이유는
 * 동시 요청이 유니크 제약에 걸렸을 때 같은 트랜잭션 안에서는 재조회가 불가능하기 때문이다
 * (예외로 rollback-only가 마킹된다). 같은 빈의 메서드를 부르면 프록시를 안 타 분리 효과도 없다.
 */
@Service
@RequiredArgsConstructor
public class MissionAssignService {

  private static final int MISSION_COUNT = 20;

  private final MissionSpotRepository missionSpotRepository;
  private final SpotRepository spotRepository;

  /**
   * 회원·시군구당 한 번만 뽑아 고정한다. 매번 새로 뽑으면 방문 인증 진행률이 의미를 잃는다.
   * 후보가 가장 적은 횡성군도 이미지 있는 관광지가 29곳이라 20개는 채워진다.
   */
  @Transactional
  public void assign(Long memberId, Integer sigunguCd) {
    List<Long> candidates = new ArrayList<>(spotRepository.findContentIdsWithImageBySigungu(sigunguCd));
    Collections.shuffle(candidates);

    List<Long> picked = candidates.subList(0, Math.min(MISSION_COUNT, candidates.size()));

    missionSpotRepository.saveAll(
      picked.stream()
        .map(contentId -> MissionSpot.of(memberId, sigunguCd, contentId))
        .toList()
    );
  }
}
