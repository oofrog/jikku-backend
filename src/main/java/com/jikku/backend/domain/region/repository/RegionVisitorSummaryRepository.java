package com.jikku.backend.domain.region.repository;

import com.jikku.backend.domain.region.entity.RegionVisitorSummary;
import com.jikku.backend.domain.region.entity.RegionVisitorSummaryId;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegionVisitorSummaryRepository
        extends JpaRepository<RegionVisitorSummary, RegionVisitorSummaryId> {

    // 응답에 시군구명이 들어가므로 join fetch로 N+1을 막는다
    @Query("""
            SELECT r FROM RegionVisitorSummary r
            JOIN FETCH r.sigungu
            WHERE r.month = :month
            ORDER BY r.rankAsc ASC
            """)
    List<RegionVisitorSummary> findByMonthOrderByRank(@Param("month") Short month, Limit limit);
}
