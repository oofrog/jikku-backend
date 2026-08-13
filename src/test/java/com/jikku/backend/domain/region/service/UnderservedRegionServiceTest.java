package com.jikku.backend.domain.region.service;

import com.jikku.backend.domain.region.dto.UnderservedRegionListResponse;
import com.jikku.backend.domain.region.dto.UnderservedRegionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UnderservedRegionServiceTest {

    @Autowired
    UnderservedRegionService underservedRegionService;

    @Test
    @DisplayName("이번 달 방문자 하위 6개 시군구를 순위 오름차순으로 준다")
    void returnsSixRegionsOrderedByRank() {
        UnderservedRegionListResponse response = underservedRegionService.getUnderservedRegions();

        assertThat(response.month()).isEqualTo(LocalDate.now().getMonthValue());
        assertThat(response.content()).hasSize(6);
        assertThat(response.content())
                .extracting(UnderservedRegionResponse::rankAsc)
                .containsExactly(1, 2, 3, 4, 5, 6);
    }

    @Test
    @DisplayName("시군구명이 함께 채워진다 (join fetch)")
    void fillsSigunguName() {
        assertThat(underservedRegionService.getUnderservedRegions().content())
                .allSatisfy(region -> {
                    assertThat(region.sigunguNm()).isNotBlank();
                    // 강원 시군구 코드는 51로 시작한다
                    assertThat(region.sigunguCd()).isBetween(51000, 51999);
                });
    }
}
