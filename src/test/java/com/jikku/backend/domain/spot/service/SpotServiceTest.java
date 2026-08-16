package com.jikku.backend.domain.spot.service;

import com.jikku.backend.domain.spot.dto.SpotSummaryResponse;
import com.jikku.backend.domain.spot.exception.SpotErrorCode;
import com.jikku.backend.global.exception.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
@SpringBootTest
class SpotServiceTest {

    @Autowired
    SpotService spotService;

    @Test
    @DisplayName("오늘의 추천은 같은 날 반복 호출해도 순서까지 같다")
    void todaySpotsAreStableWithinTheSameDay() {
        List<SpotSummaryResponse> first = spotService.getTodaySpots().content();
        List<SpotSummaryResponse> second = spotService.getTodaySpots().content();

        // 배열 순서가 흔들리면 프론트에서 카드가 뒤바뀌어 보인다
        assertThat(first).containsExactlyElementsOf(second);
    }

    @Test
    @DisplayName("오늘의 추천은 10개이고 모두 대표 이미지가 있다")
    void todaySpotsAlwaysHaveImage() {
        List<SpotSummaryResponse> spots = spotService.getTodaySpots().content();

        assertThat(spots).hasSize(10);
        assertThat(spots).allSatisfy(spot -> assertThat(spot.firstImage()).isNotBlank());
        assertThat(spots).extracting(SpotSummaryResponse::contentId).doesNotHaveDuplicates();
        assertThat(spots).allSatisfy(spot -> assertThat(spot.sigunguNm()).isNotBlank());
    }

    @Test
    @DisplayName("없는 contentId를 조회하면 SPOT404_1")
    void unknownContentIdThrows() {
        assertThatThrownBy(() -> spotService.getSpot(999_999_999L))
                .isInstanceOf(BaseException.class)
                .extracting(e -> ((BaseException) e).getErrorCode())
                .isEqualTo(SpotErrorCode.SPOT_NOT_FOUND);
    }
}
