package com.jikku.backend.domain.map.dto;

import com.jikku.backend.domain.map.entity.FillMap;
import lombok.Builder;

@Builder
public record EmdFillResponse(
  Long fillMapId,
  Integer sigunguCd,
  Long emdId,
  String mapType,
  String fillType,
  String color,
  String imgUrl
) {

  public static EmdFillResponse from(FillMap fillMap) {
    return EmdFillResponse.builder()
      .fillMapId(fillMap.getFillMapId())
      .sigunguCd(fillMap.getSigungu().getSigunguCd())
      .emdId(fillMap.getEmd().getEmdId())
      .mapType(fillMap.getMapType().name())
      .fillType(fillMap.getFillType().name())
      .color(fillMap.getColor())
      .imgUrl(fillMap.getImgUrl())
      .build();
  }
}
