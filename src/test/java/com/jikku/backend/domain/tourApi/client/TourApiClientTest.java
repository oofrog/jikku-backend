package com.jikku.backend.domain.tourApi.client;

import com.jikku.backend.domain.tourApi.dto.VisitorDDItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest   // 스프링 컨텍스트를 띄워서, 우리가 만든 '진짜' 빈으로 실제 호출한다
class TourApiClientTest {

    @Autowired
    TourApiClient tourApiClient;   // 우리가 만든 client 를 주입받음

    @Test
    @DisplayName("하루치 방문자 데이터를 호출해 콘솔에 출력한다 (눈으로 확인용)")
    void visitorDDTest() {
        // given: 검증된 최신일 + 소량
        String baseYmd = "20240623";
        int numOfRows = 10;

        // when: 실제 TourAPI 호출
        List<VisitorDDItem> items = tourApiClient.getVisitorData(baseYmd, numOfRows);

        // then: 결과를 눈으로 확인 (자동 판정 없음)
        System.out.println("===== 받은 건수: " + items.size() + " =====");
        for (VisitorDDItem item : items) {
            System.out.println(item);   // record 라 toString 이 자동 → 8개 필드가 다 보임
        }
    }
}