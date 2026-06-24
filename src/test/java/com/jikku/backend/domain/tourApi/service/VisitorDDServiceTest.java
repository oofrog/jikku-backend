package com.jikku.backend.domain.tourApi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}