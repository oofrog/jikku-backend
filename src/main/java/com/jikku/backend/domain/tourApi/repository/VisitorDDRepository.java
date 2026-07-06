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

    // "20240623" 같은 문자열을 LocalDate 로 바꾸기 위한 포맷
    private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyyMMdd");

    public VisitorDDRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // upsert SQL: 같은 (base_ymd, sigungu_cd, tou_div_cd) 가 이미 있으면 tou_num 만 덮어쓴다.
    //            → 같은 날을 두 번 적재해도 행이 안 늘어난다(멱등성).
    private static final String UPSERT_SQL = """
            INSERT INTO visitor_daily
                (base_ymd, sigungu_cd, daywk_div_cd, tou_div_cd, tou_num)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (base_ymd, sigungu_cd, tou_div_cd)
            DO UPDATE SET tou_num = EXCLUDED.tou_num
            """;

    // 거른 item 리스트를 한 번에 저장한다. (batchUpdate = 여러 건을 묶어 효율적으로)
    public void saveAll(List<VisitorDDItem> items) {
        jdbcTemplate.batchUpdate(UPSERT_SQL, items, items.size(), (ps, item) -> {
            // 문자열로 온 값들을 DB 컬럼 타입에 맞게 변환해서 ? 자리에 채운다
            ps.setDate(1, Date.valueOf(LocalDate.parse(item.baseYmd(), YMD))); // "20240623" → DATE
            ps.setInt(2, Integer.parseInt(item.signguCode()));                  // "51110" → int
            ps.setInt(3, Integer.parseInt(item.daywkDivCd()));                  // 요일코드
            ps.setInt(4, Integer.parseInt(item.touDivCd()));                    // 관광객구분
            ps.setBigDecimal(5, new java.math.BigDecimal(item.touNum()));       // "103876.5" → NUMERIC
        });
    }

    // visitor_daily 에 이미 저장된 '날짜들'을 Set 으로 읽는다. (이어받기용)
    public Set<LocalDate> findSavedDates() {
        List<LocalDate> dates = jdbcTemplate.queryForList(
                "SELECT DISTINCT base_ymd FROM visitor_daily", LocalDate.class);
        return new HashSet<>(dates);
    }
}