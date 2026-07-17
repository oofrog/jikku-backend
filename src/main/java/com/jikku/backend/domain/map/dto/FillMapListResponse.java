package com.jikku.backend.domain.map.dto;

import java.util.List;

public record FillMapListResponse(
  List<FillMapResponse> content
) {

  public static FillMapListResponse from(List<FillMapResponse> content) {
    return new FillMapListResponse(content);
  }
}
