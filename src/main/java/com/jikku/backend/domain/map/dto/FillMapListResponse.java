package com.jikku.backend.domain.map.dto;

import java.util.List;

public record FillMapListResponse<T>(
  List<T> content
) {
  public static <T> FillMapListResponse<T> from(List<T> content) {
    return new FillMapListResponse<>(content);
  }
}
