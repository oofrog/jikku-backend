package com.jikku.backend.domain.region.dto;

import com.jikku.backend.domain.region.entity.Emd;

public record EmdResponse(
  Long emdId,
  String emdNm
) {

  public static EmdResponse from(Emd emd) {
    return new EmdResponse(
      emd.getEmdId(),
      emd.getEmdNm()
    );
  }
}
