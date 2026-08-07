package com.jikku.backend.domain.tourApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// /KorService2/detailCommon2 응답. 목록 오퍼레이션에 없는 overview를 얻으려고만 쓴다.
@JsonIgnoreProperties(ignoreUnknown = true)
public record DetailItem(
        String contentid,
        String overview
) {
}
