package com.jikku.backend.domain.tourApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// /DataLabService/locgoRegnVisitrDDList 응답
@JsonIgnoreProperties(ignoreUnknown = true)
public record VisitorDDItem(
        String signguCode,
        String signguNm,
        String daywkDivCd,
        String daywkDivNm,
        String touDivCd,
        String touDivNm,
        String touNum,
        String baseYmd
) {}