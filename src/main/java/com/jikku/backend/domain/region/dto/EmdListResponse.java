package com.jikku.backend.domain.region.dto;

import java.util.List;

public record EmdListResponse(
  List<EmdResponse> content
) {

  public static EmdListResponse from(List<EmdResponse> content) {
    return new EmdListResponse(content);
  }
}
