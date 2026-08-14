package com.jikku.backend.domain.tourApi.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@Disabled("3개년 적재 완료. 재적재 필요 시에만 이 줄 제거하고 수동 실행")
@Tag("integration")
@SpringBootTest
class VisitorDDServiceTest {

    @Autowired
    VisitorDDService visitorDDService;   // 우리가 만든 적재 서비스 주입

    @Test
    @DisplayName("하루치를 호출→강원필터→visitor_daily 저장한다")
    void ingestOneDayTest() {
        // given: 강원이 포함되도록 numOfRows 를 크게 (전국 다 받아야 51xxx 가 들어옴)
        String baseYmd = "20240623";
        int numOfRows = 1000;

        // when: 호출 → 필터 → 저장
        int saved = visitorDDService.ingestOneDay(baseYmd, numOfRows);

        // then: 저장 건수를 콘솔로 확인 (자동 판정 없이 눈으로)
        System.out.println("===== 저장된 강원 건수: " + saved + " =====");
    }

    @Test
    @DisplayName("3개년 전체 적재 (20230525 ~ 20260525) - 개발 기준 30일 전 데이터만 확인 가능")
    void ingestThreeYearsTest() {
        LocalDate start = LocalDate.of(2023, 5, 25);
        LocalDate end   = LocalDate.of(2026, 5, 25);

        visitorDDService.ingestRange(start, end, 1000);
    }
}