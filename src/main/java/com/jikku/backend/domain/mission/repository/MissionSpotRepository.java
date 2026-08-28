package com.jikku.backend.domain.mission.repository;

import com.jikku.backend.domain.mission.entity.MissionSpot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MissionSpotRepository extends JpaRepository<MissionSpot, Long> {

  Optional<MissionSpot> findByMissionSpotIdAndMemberId(Long missionSpotId, Long memberId);

  /**
   * 미션은 회원·시군구당 한 번만 뽑아 고정하므로, 적재 순서(= id 순)가 곧 노출 순서다.
   */
  List<MissionSpot> findByMemberIdAndSigunguCdOrderByMissionSpotId(Long memberId, Integer sigunguCd);

  @Query("""
      SELECT COUNT(ms)
      FROM MissionSpot ms
      WHERE ms.memberId = :memberId
        AND ms.sigunguCd = :sigunguCd
        AND ms.isCompleted = true
      """)
  long countCompletedByMemberIdAndSigunguCd(
    @Param("memberId") Long memberId,
    @Param("sigunguCd") Integer sigunguCd
  );
}
