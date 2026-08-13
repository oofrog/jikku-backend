package com.jikku.backend.domain.spot.repository;

import com.jikku.backend.domain.spot.entity.Festival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 축제 조회. 적재는 tourApi 도메인의 FestivalIngestRepository가 JdbcTemplate으로 한다.
 * 응답에 시군구명이 들어가므로 두 쿼리 모두 join fetch로 N+1을 막는다.
 */
public interface FestivalRepository extends JpaRepository<Festival, Long> {

    // 종료일이 없는 축제는 언제 끝나는지 모르는 것이라 제외하지 않는다 (현재 데이터엔 없지만 재적재로 생길 수 있다)
    @Query("""
            SELECT f FROM Festival f
            JOIN FETCH f.sigungu
            WHERE f.eventEndDate IS NULL OR f.eventEndDate >= :today
            ORDER BY f.eventStartDate ASC, f.contentId ASC
            """)
    List<Festival> findNotEndedOrderByStartDate(@Param("today") LocalDate today);

    @Query("SELECT f FROM Festival f JOIN FETCH f.sigungu WHERE f.contentId = :contentId")
    Optional<Festival> findWithSigungu(@Param("contentId") Long contentId);
}
