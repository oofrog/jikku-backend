package com.jikku.backend.domain.map.dto;

public record FillMapResponse(
  Long fillMapId,
  Long sigunguId,
  Long emdId,
  String mapType,
  String fillType,
  String color,
  String imgUrl
) {}
