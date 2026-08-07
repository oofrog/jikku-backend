package com.jikku.backend.domain.tourApi.service;

import com.jikku.backend.domain.tourApi.repository.FestivalRepository;
import com.jikku.backend.domain.tourApi.repository.SpotRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@Disabled("적재 완료(관광지 1356 / 축제 55). 재적재 필요 시에만 이 줄 제거하고 수동 실행")
// 다른 테스트와 달리 실제 Supabase·TourAPI에 붙는 적재 작업이다.
// src/test/resources/application.yml이 더미 DB(localhost)를 가리키므로 여기서만 실제 접속 정보로 되돌린다.
@SpringBootTest(properties = {
        "spring.datasource.url=${DB_URL}",
        "spring.datasource.username=${DB_USERNAME}",
        "spring.datasource.password=${DB_PASSWORD}"
})
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
