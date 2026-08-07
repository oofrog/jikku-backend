package com.jikku.backend.domain.tourApi.service;

import com.jikku.backend.domain.tourApi.repository.FestivalRepository;
import com.jikku.backend.domain.tourApi.repository.SpotRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@Disabled("수동 적재용. 실행할 때만 이 줄 제거")
@SpringBootTest
class SpotFestivalIngestServiceTest {

    @Autowired
    SpotIngestService spotIngestService;

    @Autowired
    FestivalIngestService festivalIngestService;

    @Autowired
    SpotRepository spotRepository;

    @Autowired
    FestivalRepository festivalRepository;

    @Test
    @DisplayName("강원 관광지 전체를 적재한다 (1,356건 / 1000건씩 2페이지)")
    void ingestSpots() {
        int saved = spotIngestService.ingestAll(1000);

        System.out.println("===== 관광지 저장: " + saved + "건, 테이블 총 " + spotRepository.count() + "건 =====");
    }

    @Test
    @DisplayName("올해 시작하는 강원 축제를 적재한다 (54건)")
    void ingestFestivals() {
        int saved = festivalIngestService.ingestFrom(LocalDate.of(2026, 1, 1), 100);

        System.out.println("===== 축제 저장: " + saved + "건, 테이블 총 " + festivalRepository.count() + "건 =====");
    }

    @Test
    @DisplayName("재실행해도 건수가 늘지 않는다 (content_id upsert 멱등성)")
    void ingestIsIdempotent() {
        spotIngestService.ingestAll(1000);
        int afterFirst = spotRepository.count();

        spotIngestService.ingestAll(1000);
        int afterSecond = spotRepository.count();

        System.out.println("===== 1회차: " + afterFirst + "건, 2회차: " + afterSecond + "건 =====");
        org.assertj.core.api.Assertions.assertThat(afterSecond).isEqualTo(afterFirst);
    }
}
