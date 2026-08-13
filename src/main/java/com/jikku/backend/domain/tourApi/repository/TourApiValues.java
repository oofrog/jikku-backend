package com.jikku.backend.domain.tourApi.repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// TourAPI는 값이 없을 때 null이 아니라 빈 문자열을 준다(관광지 firstimage의 12%).
// 그대로 넘기면 숫자·날짜 파싱이 깨지고 DB엔 ""가 남으므로 여기서 null로 정규화한다.
final class TourApiValues {

    private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter YMD_HMS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private TourApiValues() {
    }

    static String text(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    static BigDecimal decimal(String value) {
        String trimmed = text(value);
        return trimmed == null ? null : new BigDecimal(trimmed);
    }

    static Timestamp dateTime(String value) {
        String trimmed = text(value);
        return trimmed == null ? null : Timestamp.valueOf(LocalDateTime.parse(trimmed, YMD_HMS));
    }

    static Date date(String value) {
        String trimmed = text(value);
        return trimmed == null ? null : Date.valueOf(LocalDate.parse(trimmed, YMD));
    }
}
