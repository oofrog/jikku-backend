package com.jikku.backend.domain.badge.entity;

import com.jikku.backend.domain.badge.enums.BadgeType;
import com.jikku.backend.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
  name = "badge",
  uniqueConstraints = {
    @UniqueConstraint(
      name = "uk_badge_member_badge_no",
      columnNames = {"member_id", "badge_no"}
    )
  }
)
public class Badge {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long badgeId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BadgeType badgeType;

  @Column(nullable = false)
  private String badgeNo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  private Badge(Member member, BadgeType badgeType, String badgeNo) {
    this.member = member;
    this.badgeType = badgeType;
    this.badgeNo = badgeNo;
  }

  public static Badge of(Member member, BadgeType badgeType, String badgeNo) {
    return new Badge(member, badgeType, badgeNo);
  }
}
