package com.jikku.backend.domain.tourApi.service;

import com.jikku.backend.domain.tourApi.repository.FestivalRepository;
import com.jikku.backend.domain.tourApi.repository.SpotRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@Disabled("축제 55건 완료, 관광지 845/1356 완료. 남은 511건은 다음 날 한도로 이어서 실행")
@SpringBootTest
class OverviewIngestServiceTest {

    @Autowired
    OverviewIngestService overviewIngestService;

    @Autowired
    SpotRepository spotRepository;

    @Autowired
    FestivalRepository festivalRepository;

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
