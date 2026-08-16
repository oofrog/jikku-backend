package com.jikku.backend.domain.spot.repository;

import com.jikku.backend.domain.spot.entity.Spot;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 관광지 조회. 적재는 tourApi 도메인의 SpotIngestRepository가 JdbcTemplate으로 한다.
 */
public interface SpotRepository extends JpaRepository<Spot, Long> {

  /**
   * 오늘의 추천 후보. 전체를 읽지 않고 id만 가져와 자바에서 뽑는다(1,192건이라 가볍다).
   * ORDER BY는 노출 순서와 무관하다. 이 배열을 날짜 시드로 다시 섞기 때문이다.
   * DB가 행 순서를 보장하지 않아서, 정렬해두지 않으면 같은 시드로도 결과가 달라진다.
   */
  @Query("SELECT s.contentId FROM Spot s WHERE s.firstImage IS NOT NULL ORDER BY s.contentId")
  List<Long> findContentIdsWithImage();

  @Query("SELECT s FROM Spot s JOIN FETCH s.sigungu WHERE s.contentId IN :contentIds")
  List<Spot> findAllWithSigungu(@Param("contentIds") Collection<Long> contentIds);

  @Query("SELECT s FROM Spot s JOIN FETCH s.sigungu WHERE s.contentId = :contentId")
  Optional<Spot> findWithSigungu(@Param("contentId") Long contentId);
}
