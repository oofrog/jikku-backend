package com.jikku.backend.domain.spot.repository;

import com.jikku.backend.domain.spot.entity.Festival;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 적재된 축제 데이터를 그대로 읽는 조회 테스트(쓰기 없음).
 * 건수는 날짜에 따라 변하므로 개수 대신 정렬·필터 성질만 검증한다.
 */
@SpringBootTest
class FestivalRepositoryTest {

    @Autowired
    FestivalRepository festivalRepository;

    @Test
    @DisplayName("지난 축제는 빠지고 시작일 오름차순으로 나온다")
    void excludesEndedAndSortsByStartDate() {
        LocalDate today = LocalDate.now();

        List<Festival> festivals = festivalRepository.findNotEndedOrderByStartDate(today);

        assertThat(festivals).isNotEmpty();
        assertThat(festivals).allSatisfy(festival ->
                assertThat(festival.getEventEndDate()).isNotNull().isAfterOrEqualTo(today));
        assertThat(festivals).isSortedAccordingTo(
                (a, b) -> a.getEventStartDate().compareTo(b.getEventStartDate()));
    }

    @Test
    @DisplayName("목록 조회가 시군구를 함께 가져온다 (N+1 방지)")
    void fetchesSigunguTogether() {
        List<Festival> festivals = festivalRepository.findNotEndedOrderByStartDate(LocalDate.now());

        // join fetch가 빠지면 프록시 상태로 넘어와 세션 밖에서 터진다
        assertThat(festivals).allSatisfy(festival ->
                assertThat(festival.getSigungu().getSigunguNm()).isNotBlank());
    }

    @Test
    @DisplayName("없는 contentId는 빈 결과를 준다")
    void returnsEmptyForUnknownContentId() {
        assertThat(festivalRepository.findWithSigungu(999_999_999L)).isEmpty();
    }
}
