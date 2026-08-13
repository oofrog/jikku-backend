package com.jikku.backend.domain.spot.repository;

import com.jikku.backend.domain.spot.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 관광지 조회. 적재는 tourApi 도메인의 SpotIngestRepository가 JdbcTemplate으로 한다.
 */
public interface SpotRepository extends JpaRepository<Spot, Long> {

    // 오늘의 추천 후보. 전체를 읽지 않고 id만 가져와 자바에서 뽑는다(1,192건이라 가볍다).
    @Query("SELECT s.contentId FROM Spot s WHERE s.firstImage IS NOT NULL")
    List<Long> findContentIdsWithImage();

    @Query("SELECT s FROM Spot s JOIN FETCH s.sigungu WHERE s.contentId IN :contentIds")
    List<Spot> findAllWithSigungu(@Param("contentIds") Collection<Long> contentIds);

    @Query("SELECT s FROM Spot s JOIN FETCH s.sigungu WHERE s.contentId = :contentId")
    Optional<Spot> findWithSigungu(@Param("contentId") Long contentId);
}
