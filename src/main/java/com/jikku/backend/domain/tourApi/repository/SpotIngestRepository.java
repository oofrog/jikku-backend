package com.jikku.backend.domain.tourApi.repository;

import com.jikku.backend.domain.tourApi.dto.SpotItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SpotIngestRepository implements OverviewRepository {

    private final JdbcTemplate jdbcTemplate;

    // overview는 detailCommon2로 따로 채우는 컬럼이라, 목록을 재적재해도 덮이지 않도록 SET에서 뺀다.
    private static final String UPSERT_SQL = """
            INSERT INTO spot
                (content_id, sigungu_cd, title, addr1, map_x, map_y,
                 first_image, lcls_systm1, lcls_systm2, lcls_systm3, modified_time)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (content_id) DO UPDATE SET
                sigungu_cd    = EXCLUDED.sigungu_cd,
                title         = EXCLUDED.title,
                addr1         = EXCLUDED.addr1,
                map_x         = EXCLUDED.map_x,
                map_y         = EXCLUDED.map_y,
                first_image   = EXCLUDED.first_image,
                lcls_systm1   = EXCLUDED.lcls_systm1,
                lcls_systm2   = EXCLUDED.lcls_systm2,
                lcls_systm3   = EXCLUDED.lcls_systm3,
                modified_time = EXCLUDED.modified_time,
                ingested_at   = now()
            """;

    public SpotIngestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveAll(List<SpotItem> items) {
        if (items.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate(UPSERT_SQL, items, items.size(), (ps, item) -> {
            ps.setLong(1, Long.parseLong(item.contentid()));
            ps.setInt(2, item.sigunguCd());
            ps.setString(3, item.title());
            ps.setString(4, TourApiValues.text(item.addr1()));
            ps.setBigDecimal(5, TourApiValues.decimal(item.mapx()));
            ps.setBigDecimal(6, TourApiValues.decimal(item.mapy()));
            ps.setString(7, TourApiValues.text(item.firstimage()));
            ps.setString(8, TourApiValues.text(item.lclsSystm1()));
            ps.setString(9, TourApiValues.text(item.lclsSystm2()));
            ps.setString(10, TourApiValues.text(item.lclsSystm3()));
            ps.setTimestamp(11, TourApiValues.dateTime(item.modifiedtime()));
        });
    }

    public int count() {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM spot", Integer.class);
        return count == null ? 0 : count;
    }

    // 방문자 적은 시군구부터 채운다. 소외지역 추천에 먼저 노출될 대상이라 한도가 걸려도 화면이 빈 채로 남지 않는다.
    private static final String WITHOUT_OVERVIEW_SQL = """
            SELECT s.content_id
            FROM spot s
            JOIN (
                SELECT sigungu_cd, avg(rank_asc) AS mean_rank
                FROM region_visitor_summary
                GROUP BY sigungu_cd
            ) v ON v.sigungu_cd = s.sigungu_cd
            WHERE s.overview IS NULL
            ORDER BY v.mean_rank, s.content_id
            LIMIT ?
            """;

    @Override
    public List<Long> findContentIdsWithoutOverview(int limit) {
        return jdbcTemplate.queryForList(WITHOUT_OVERVIEW_SQL, Long.class, limit);
    }

    @Override
    public void updateOverview(long contentId, String overview) {
        jdbcTemplate.update("UPDATE spot SET overview = ? WHERE content_id = ?", overview, contentId);
    }

    @Override
    public int countWithoutOverview() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM spot WHERE overview IS NULL", Integer.class);
        return count == null ? 0 : count;
    }
}
