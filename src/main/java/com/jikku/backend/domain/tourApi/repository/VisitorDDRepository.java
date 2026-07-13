package com.jikku.backend.domain.tourApi.repository;

import com.jikku.backend.domain.tourApi.dto.VisitorDDItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class VisitorDDRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyyMMdd");

    public VisitorDDRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 같은 (base_ymd, sigungu_cd, tou_div_cd)를 다시 적재해도 tou_num만 갱신되어 멱등하다.
    private static final String UPSERT_SQL = """
            INSERT INTO visitor_daily
                (base_ymd, sigungu_cd, daywk_div_cd, tou_div_cd, tou_num)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (base_ymd, sigungu_cd, tou_div_cd)
            DO UPDATE SET tou_num = EXCLUDED.tou_num
            """;

    public void saveAll(List<VisitorDDItem> items) {
        jdbcTemplate.batchUpdate(UPSERT_SQL, items, items.size(), (ps, item) -> {
            ps.setDate(1, Date.valueOf(LocalDate.parse(item.baseYmd(), YMD)));
            ps.setInt(2, Integer.parseInt(item.signguCode()));
            ps.setInt(3, Integer.parseInt(item.daywkDivCd()));
            ps.setInt(4, Integer.parseInt(item.touDivCd()));
            ps.setBigDecimal(5, new java.math.BigDecimal(item.touNum()));
        });
    }

    // 이어받기용: 이미 저장된 날짜 집합
    public Set<LocalDate> findSavedDates() {
        List<LocalDate> dates = jdbcTemplate.queryForList(
                "SELECT DISTINCT base_ymd FROM visitor_daily", LocalDate.class);
        return new HashSet<>(dates);
    }
}