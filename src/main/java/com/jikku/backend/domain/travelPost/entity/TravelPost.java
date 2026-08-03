package com.jikku.backend.domain.travelPost.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "travel_post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelPost {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "travel_post_id")
  private Long travelPostId;

  @Column(nullable = false)
  private String title;

  @Column(name = "first_image")
  private String firstImage;
}
