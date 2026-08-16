package com.jikku.backend.domain.badge.repository;

import com.jikku.backend.domain.badge.entity.Badge;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

  @Query("""
      SELECT mb.badge
      FROM MemberBadge mb
      WHERE mb.member.memberId = :memberId
      ORDER BY mb.badge.badgeId
      """)
  List<Badge> findAllByMemberId(@Param("memberId") Long memberId);
}
