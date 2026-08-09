package com.jikku.backend.domain.travelPost.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record TravelPostCreateRequest(
  @NotNull(message = "emdId는 필수입니다.")
  Long emdId,

  @NotBlank(message = "title은 필수입니다.")
  String title,

  @NotNull(message = "logDate는 필수입니다.")
  LocalDate logDate,

  @Valid
  @NotNull(message = "blocks는 필수입니다.")
  List<TravelPostBlockCreateRequest> blocks
) {
}
