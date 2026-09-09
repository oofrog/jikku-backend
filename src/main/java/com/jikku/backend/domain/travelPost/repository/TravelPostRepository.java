package com.jikku.backend.domain.travelPost.repository;

import com.jikku.backend.domain.travelPost.entity.TravelPost;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.jikku.backend.domain.region.entity.Sigungu;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

  @Query("""
  select distinct tp.emd.sigungu
  from TravelPost tp
  where tp.memberId = :memberId
  order by tp.emd.sigungu.sigunguNm asc
  """)
  List<Sigungu> findDistinctSigunguByMemberId(
    @Param("memberId") Long memberId
  );
}
