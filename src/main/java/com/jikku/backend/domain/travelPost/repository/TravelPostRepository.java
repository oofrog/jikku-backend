package com.jikku.backend.domain.travelPost.repository;

import com.jikku.backend.domain.travelPost.entity.TravelPost;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelPostRepository
  extends JpaRepository<TravelPost, Long> {

  Optional<TravelPost> findByTravelPostIdAndMemberId(
    Long travelPostId,
    Long memberId
  );

  List<TravelPost>
  findByMemberIdAndEmd_Sigungu_SigunguCdOrderByLogDateDescTravelPostIdDesc(
    Long memberId,
    Integer sigunguCd
  );

  List<TravelPost>
  findByMemberIdAndEmd_Sigungu_SigunguCdAndLogDateOrderByTravelPostIdDesc(
    Long memberId,
    Integer sigunguCd,
    LocalDate logDate
  );
}
