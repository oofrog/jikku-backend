package com.jikku.backend.domain.badge.repository;

import com.jikku.backend.domain.badge.entity.Badge;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

  List<Badge> findAllByMember_MemberIdOrderByBadgeId(Long memberId);
}
