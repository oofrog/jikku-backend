package com.jikku.backend.domain.travelPost.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "travel_post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelPost {

  @Id
  @Column(name = "travel_post_id")
  private Long travelPostId;

  @Column(nullable = false)
  private String title;

  @Column(name = "first_image")
  private String firstImage;
}
