package com.jikku.backend.domain.region.repository;

import com.jikku.backend.domain.region.entity.Emd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 읍면동 마스터 저장소 (JPA, 읽기 전용 참조 데이터).
 */
public interface EmdRepository extends JpaRepository<Emd, Long> {

    // 시군구별 읍면동 목록 조회
    List<Emd> findBySigungu_SigunguCd(Integer sigunguCd);
}
