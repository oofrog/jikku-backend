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
   * 첫 조회 판정용. 남은 미션 목록으로 판정하면 20개를 다 인증한 회원에게 미션이 새로 적재된다.
   */
  boolean existsByMemberIdAndSigunguCd(Long memberId, Integer sigunguCd);

  /**
   * 아직 인증하지 않은 미션만. 미션은 회원·시군구당 한 번만 뽑아 고정하므로 적재 순서(= id 순)가 곧 노출 순서다.
   */
  List<MissionSpot> findByMemberIdAndSigunguCdAndIsCompletedFalseOrderByMissionSpotId(
    Long memberId,
    Integer sigunguCd
  );

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
