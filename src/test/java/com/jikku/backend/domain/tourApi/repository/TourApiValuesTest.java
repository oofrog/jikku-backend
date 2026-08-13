package com.jikku.backend.domain.tourApi.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TourApiValuesTest {

    @Test
    @DisplayName("빈 문자열은 null로 정규화된다 (TourAPI는 값이 없을 때 null 대신 \"\"를 준다)")
    void blankBecomesNull() {
        assertThat(TourApiValues.text("")).isNull();
        assertThat(TourApiValues.text("   ")).isNull();
        assertThat(TourApiValues.text(null)).isNull();
        assertThat(TourApiValues.text("가람리조트")).isEqualTo("가람리조트");

        assertThat(TourApiValues.decimal("")).isNull();
        assertThat(TourApiValues.dateTime("")).isNull();
        assertThat(TourApiValues.date("")).isNull();
    }

    @Test
    @DisplayName("좌표는 소수점 12자리까지 손실 없이 변환된다")
    void decimalKeepsPrecision() {
        assertThat(TourApiValues.decimal("128.895500767487"))
                .isEqualByComparingTo(new BigDecimal("128.895500767487"));
    }

    @Test
    @DisplayName("yyyyMMddHHmmss와 yyyyMMdd 형식을 각각 변환한다")
    void parsesTourApiDateFormats() {
        assertThat(TourApiValues.dateTime("20251117152045"))
                .hasToString("2025-11-17 15:20:45.0");
        assertThat(TourApiValues.date("20260404"))
                .hasToString("2026-04-04");
    }
}
