package com.jikku.backend.domain.region.service;

import com.jikku.backend.domain.region.dto.UnderservedRegionListResponse;
import com.jikku.backend.domain.region.dto.UnderservedRegionResponse;
import com.jikku.backend.domain.region.repository.RegionVisitorSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UnderservedRegionService {

    private static final int UNDERSERVED_COUNT = 6;

    private final RegionVisitorSummaryRepository regionVisitorSummaryRepository;

    /**
     * 이번 달 방문자가 적은 하위 6개 시군구.
     * 기준 월을 파라미터로 받지 않고 서버 시각으로 정하므로, 어느 달로 계산했는지 응답에 함께 담는다.
     */
    @Transactional(readOnly = true)
    public UnderservedRegionListResponse getUnderservedRegions() {
        int month = LocalDate.now().getMonthValue();

        return UnderservedRegionListResponse.of(
                month,
                regionVisitorSummaryRepository
                        .findByMonthOrderByRank((short) month, Limit.of(UNDERSERVED_COUNT))
                        .stream()
                        .map(UnderservedRegionResponse::from)
                        .toList()
        );
    }
}
