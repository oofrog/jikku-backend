package com.jikku.backend.domain.tourApi.service;

import com.jikku.backend.domain.tourApi.repository.FestivalIngestRepository;
import com.jikku.backend.domain.tourApi.repository.SpotIngestRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@Disabled("전량 적재 완료(관광지 1356 / 축제 55). TourAPI 데이터 갱신분을 다시 받을 때만 실행")
@Tag("integration")
@SpringBootTest
class OverviewIngestServiceTest {

    @Autowired
    OverviewIngestService overviewIngestService;

    @Autowired
    SpotIngestRepository spotRepository;

    @Autowired
    FestivalIngestRepository festivalRepository;

    @Test
    @DisplayName("남은 한도만큼 overview를 채운다 (축제 우선 → 관광지는 방문자 적은 시군구 순)")
    void ingestOverviews() {
        int before = spotRepository.countWithoutOverview() + festivalRepository.countWithoutOverview();

        int filled = overviewIngestService.ingest(900);

        System.out.println("===== 시작 전 미적재: " + before + "건, 이번에 채움: " + filled + "건 =====");
        System.out.println("===== 남은 미적재: 관광지 " + spotRepository.countWithoutOverview()
                + "건 / 축제 " + festivalRepository.countWithoutOverview() + "건 =====");
    }
}
