package com.jikku.backend.domain.mission.repository;

import com.jikku.backend.domain.mission.entity.MissionSpot;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MissionSpotRepository extends JpaRepository<MissionSpot, Long> {

  Optional<MissionSpot> findByMissionSpotIdAndMemberId(Long missionSpotId, Long memberId);

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
