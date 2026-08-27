package com.jikku.backend.domain.map.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record SigunguFillMapListResponse(
  @JsonProperty("total_count")
  int totalCount,

  List<SigunguFillResponse> content
) {

  public static SigunguFillMapListResponse from(List<SigunguFillResponse> content) {
    return new SigunguFillMapListResponse(content.size(), content);
  }
}
