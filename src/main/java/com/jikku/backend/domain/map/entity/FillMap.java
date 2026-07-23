package com.jikku.backend.domain.map.entity;

import com.jikku.backend.domain.map.enums.FillType;
import com.jikku.backend.domain.map.enums.MapType;
import com.jikku.backend.global.entity.BaseTimeEntity;
import org.hibernate.annotations.Check;
import jakarta.persistence.*;
import lombok.*;
import com.jikku.backend.domain.region.entity.Sigungu;
import com.jikku.backend.domain.region.entity.Emd;

@Entity
@Check(constraints = "(" +
  "(" +
  "map_type = 'SIGUNGU' AND sigungu_cd IS NOT NULL AND emd_id IS NULL" +
  ") OR (" +
  "map_type = 'EMD' AND sigungu_cd IS NOT NULL AND emd_id IS NOT NULL" +
  ")" +
  ") AND (" +
  "(" +
  "fill_type = 'COLOR' AND color IS NOT NULL AND img_url IS NULL" +
  ") OR (" +
  "fill_type = 'IMAGE' AND img_url IS NOT NULL AND color IS NULL" +
  ")" +
  ")")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sigungu_cd", nullable = false)
  private Sigungu sigungu;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "emd_id")
  private Emd emd;

  public void fillWithColor(String color) {
    this.fillType = FillType.COLOR;
    this.color = color;
    this.imgUrl = null;
  }

  public void fillWithImage(String imgUrl) {
    this.fillType = FillType.IMAGE;
    this.imgUrl = imgUrl;
    this.color = null;
  }

  public static FillMap ofSigungu(
    Long memberId,
    Sigungu sigungu,
    FillType fillType,
    String color,
    String imgUrl
  ) {
    FillMap fillMap = FillMap.builder()
      .memberId(memberId)
      .sigungu(sigungu)
      .emd(null)
      .mapType(MapType.SIGUNGU)
      .build();

    if (fillType == FillType.COLOR) {
      fillMap.fillWithColor(color);
    }

    if (fillType == FillType.IMAGE) {
      fillMap.fillWithImage(imgUrl);
    }

    return fillMap;
  }

  public static FillMap ofEmd(
    Long memberId,
    Sigungu sigungu,
    Emd emd,
    FillType fillType,
    String color,
    String imgUrl
  ) {
    FillMap fillMap = FillMap.builder()
      .memberId(memberId)
      .sigungu(sigungu)
      .emd(emd)
      .mapType(MapType.EMD)
      .build();

    if (fillType == FillType.COLOR) {
      fillMap.fillWithColor(color);
    }

    if (fillType == FillType.IMAGE) {
      fillMap.fillWithImage(imgUrl);
    }

    return fillMap;
  }
}


