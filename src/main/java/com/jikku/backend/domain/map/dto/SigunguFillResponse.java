package com.jikku.backend.domain.map.dto;

import com.jikku.backend.domain.map.entity.FillMap;
import lombok.Builder;

@Builder
public record SigunguFillResponse(
  Long fillMapId,
  Integer sigunguCd,
  String sigunguNm,
  String mapType,
  String fillType,
  String color,
  String imgUrl
) {

  public static SigunguFillResponse from(FillMap fillMap) {
    return SigunguFillResponse.builder()
      .fillMapId(fillMap.getFillMapId())
      .sigunguCd(fillMap.getSigungu().getSigunguCd())
      .sigunguNm(fillMap.getSigungu().getSigunguNm())
      .mapType(fillMap.getMapType().name())
      .fillType(fillMap.getFillType().name())
      .color(fillMap.getColor())
      .imgUrl(fillMap.getImgUrl())
      .build();
  }
}
