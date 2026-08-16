package com.jikku.backend.domain.region.dto;

import lombok.Builder;

import java.util.List;

/**
 * month는 서버가 어느 달로 계산했는지 프론트가 알 수 있게 함께 내려주는 값이다.
 * (요청 파라미터로 받지 않고 서버 시각으로 정하기 때문에 응답에 명시한다)
 */
@Builder
public record UnderservedRegionListResponse(
        int month,
        List<UnderservedRegionResponse> content
) {

    public static UnderservedRegionListResponse of(int month, List<UnderservedRegionResponse> content) {
        return UnderservedRegionListResponse.builder()
                .month(month)
                .content(content)
                .build();
    }
}
