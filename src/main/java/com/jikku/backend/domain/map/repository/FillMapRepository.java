package com.jikku.backend.domain.map.repository;

import com.jikku.backend.domain.map.entity.FillMap;
import com.jikku.backend.domain.map.enums.MapType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FillMapRepository extends JpaRepository<FillMap, Long> {

  List<FillMap> findByMemberId(Long memberId);
  List<FillMap> findByMemberIdAndMapType(Long memberId, MapType mapType);

  List<FillMap> findByMemberIdAndMapTypeAndSigungu_SigunguCd(
    Long memberId,
    MapType mapType,
    Integer sigunguCd
  );

  Optional<FillMap> findByMemberIdAndMapTypeAndSigungu_SigunguCdAndEmd_EmdId(
    Long memberId,
    MapType mapType,
    Integer sigunguCd,
    Long emdId
  );
}
