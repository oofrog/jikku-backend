package com.jikku.backend.domain.travelPost.repository;

import com.jikku.backend.domain.travelPost.entity.TravelPost;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelPostRepository extends JpaRepository<TravelPost, Long> {

  @EntityGraph(attributePaths = "emd")
  List<TravelPost> findByEmd_Sigungu_SigunguCdOrderByLogDateDescTravelPostIdDesc(Integer sigunguCd);

  @EntityGraph(attributePaths = "emd")
  List<TravelPost> findByEmd_Sigungu_SigunguCdAndLogDateOrderByTravelPostIdDesc(
    Integer sigunguCd,
    LocalDate logDate
  );
}
