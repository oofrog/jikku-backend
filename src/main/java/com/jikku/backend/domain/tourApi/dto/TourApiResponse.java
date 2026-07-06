package com.jikku.backend.domain.tourApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;

// TourAPI 응답 공용
@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiResponse<T>(Response<T> response) {

    public List<T> items() {
        if (response == null || response.body() == null
                || response.body().items() == null
                || response.body().items().item() == null) {
            return Collections.emptyList();
        }
        return response.body().items().item();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response<T>(Header header, Body<T> body) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Header(String resultCode, String resultMsg) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body<T>(Items<T> items, Integer numOfRows, Integer pageNo, Integer totalCount) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Items<T>(List<T> item) {}
}
