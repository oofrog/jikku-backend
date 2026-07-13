package com.jikku.backend.domain.map.repository;

import com.jikku.backend.domain.map.entity.FillMap;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FillMapRepository extends JpaRepository<FillMap, Long> {
  List<FillMap> findByMemberId(Long memberId);
}
