package com.jikku.backend.domain.travelPost.entity;

import com.jikku.backend.domain.region.entity.Emd;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

  @Column(name = "member_id", nullable = false)
  private Long memberId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "emd_id", nullable = false)
  private Emd emd;

  @Column(name = "log_date", nullable = false)
  private LocalDate logDate;

  @Column(nullable = false)
  private String title;

  @Column(name = "first_image")
  private String firstImage;

  @OneToMany(mappedBy = "travelPost")
  private List<TravelPostBlock> blocks = new ArrayList<>();

  public static TravelPost of(
    Long memberId,
    Emd emd,
    LocalDate logDate,
    String title,
    String firstImage
  ) {
    TravelPost travelPost = new TravelPost();
    travelPost.memberId = memberId;
    travelPost.emd = emd;
    travelPost.logDate = logDate;
    travelPost.title = title;
    travelPost.firstImage = firstImage;
    return travelPost;
  }
}
