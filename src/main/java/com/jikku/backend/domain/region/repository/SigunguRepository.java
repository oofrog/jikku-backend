package com.jikku.backend.domain.region.repository;

import com.jikku.backend.domain.region.entity.Sigungu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 시군구 마스터 저장소 (JPA, 읽기 전용 참조 데이터).
 */
public interface SigunguRepository extends JpaRepository<Sigungu, Integer> {

  Optional<Sigungu> findBySigunguCd(Integer sigunguCd);
}
