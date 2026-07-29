package com.jikku.backend.domain.map.entity;

import com.jikku.backend.domain.map.dto.MapStickerRequest;
import com.jikku.backend.domain.map.dto.MapTravelPostRequest;
import com.jikku.backend.domain.map.enums.StickerType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "map_sticker")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MapSticker {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "map_sticker_id")
  private Long mapStickerId;

  @Enumerated(EnumType.STRING)
  @Column(name = "sticker_type", nullable = false)
  private StickerType stickerType;

  @Column(name = "pos_x", nullable = false)
  private Float posX;

  @Column(name = "pos_y", nullable = false)
  private Float posY;

  @Column(nullable = false)
  private Float scale;

  @Column(name = "z_index", nullable = false)
  private Integer zIndex;

  @Column(name = "member_id", nullable = false)
  private Long memberId;

  @Column(name = "travel_post_id")
  private Long travelPostId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sticker_id")
  private Sticker sticker;

  @Column(name = "sigungu_cd", nullable = false)
  private Integer sigunguCd;

  @Builder
  private MapSticker(
    Long mapStickerId,
    StickerType stickerType,
    Float posX,
    Float posY,
    Float scale,
    Integer zIndex,
    Long memberId,
    Long travelPostId,
    Sticker sticker,
    Integer sigunguCd
  ) {
    this.mapStickerId = mapStickerId;
    this.stickerType = stickerType;
    this.posX = posX;
    this.posY = posY;
    this.scale = scale;
    this.zIndex = zIndex;
    this.memberId = memberId;
    this.travelPostId = travelPostId;
    this.sticker = sticker;
    this.sigunguCd = sigunguCd;
  }

  public static MapSticker ofSticker(
    Long memberId,
    Integer sigunguCd,
    Sticker sticker,
    MapStickerRequest request
  ) {
    return MapSticker.builder()
      .stickerType(StickerType.STICKER)
      .posX(request.posX())
      .posY(request.posY())
      .scale(request.scale())
      .zIndex(request.zIndex())
      .memberId(memberId)
      .travelPostId(null)
      .sticker(sticker)
      .sigunguCd(sigunguCd)
      .build();
  }

  public static MapSticker ofTravelPost(
    Long memberId,
    Integer sigunguCd,
    Long travelPostId,
    MapTravelPostRequest request
  ) {
    return MapSticker.builder()
      .stickerType(StickerType.POST)
      .posX(request.posX())
      .posY(request.posY())
      .scale(request.scale())
      .zIndex(request.zIndex())
      .memberId(memberId)
      .travelPostId(travelPostId)
      .sticker(null)
      .sigunguCd(sigunguCd)
      .build();
  }
}
