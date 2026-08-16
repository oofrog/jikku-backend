package com.jikku.backend.domain.badge.entity;

import com.jikku.backend.domain.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.UniqueConstraint;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
  name = "member_badge",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_member_badge_member_badge", columnNames = {"member_id", "badge_id"})
  }
)
public class MemberBadge {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memberBadgeId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "badge_id", nullable = false)
  private Badge badge;
}
