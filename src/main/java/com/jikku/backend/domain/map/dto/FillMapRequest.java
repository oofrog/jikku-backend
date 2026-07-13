package com.jikku.backend.domain.map.dto;

public record FillMapRequest(
  Long sigunguId,
  Long emdId,
  String mapType,
  String fillType,
  String color,
  String imgUrl
) {}
