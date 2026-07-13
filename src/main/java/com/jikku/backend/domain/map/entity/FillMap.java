package com.jikku.backend.domain.map.entity;

import com.jikku.backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "fill_map")
public class FillMap extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long fillMapId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MapType mapType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FillType fillType;

  @Column(length = 7)
  private String color;

  private String imgUrl;

  @Column(nullable = false)
  private Long memberId;

  private Long sigunguId;

  private Long emdId;
}
