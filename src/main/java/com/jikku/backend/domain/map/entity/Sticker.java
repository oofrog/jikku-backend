package com.jikku.backend.domain.map.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "sticker")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sticker {

  @Id
  @Column(name = "sticker_id")
  private Long stickerId;

  @Column(nullable = false, length = 50)
  private String code;

  @Column(name = "sticker_url", nullable = false, length = 300)
  private String stickerUrl;
}
