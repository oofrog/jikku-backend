package com.jikku.backend.domain.travelPost.dto;

import com.jikku.backend.domain.region.entity.Sigungu;

public record TravelPostSigunguResponse(
  Integer sigunguId,
  String sigunguNm
) {
  public static TravelPostSigunguResponse from(Sigungu sigungu) {
    return new TravelPostSigunguResponse(
      sigungu.getSigunguCd(),
      sigungu.getSigunguNm()
    );
  }
}
