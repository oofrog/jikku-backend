package com.jikku.backend.domain.mission.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
  name = "mission_spot",
  uniqueConstraints = {
    @UniqueConstraint(
      name = "uk_mission_spot_member_content",
      columnNames = {"member_id", "content_id"}
    )
  }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MissionSpot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long missionSpotId;

  @Column(nullable = false)
  private Boolean isCompleted = false;

  @Column(nullable = false)
  private Long memberId;

  @Column(nullable = false)
  private Integer sigunguCd;

  @Column(nullable = false)
  private Long contentId;

  public void complete() {
    this.isCompleted = true;
  }
}
