package com.jikku.backend.domain.mission.repository;

import com.jikku.backend.domain.mission.entity.MissionSpot;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionSpotRepository extends JpaRepository<MissionSpot, Long> {

  Optional<MissionSpot> findByMissionSpotIdAndMemberId(Long missionSpotId, Long memberId);
}
