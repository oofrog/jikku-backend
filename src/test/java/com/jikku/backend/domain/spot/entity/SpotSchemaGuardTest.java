package com.jikku.backend.domain.spot.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * spot·festival은 Supabase에서 직접 만든 테이블인데 ddl-auto=update가 켜져 있어,
 * 엔티티가 DB와 어긋나면 Hibernate가 운영 스키마를 말없이 고친다.
 * 실제로 columnDefinition을 빼먹었을 때 text 6개가 varchar(255)로 좁혀진 적이 있어 회귀 테스트로 남긴다.
 * 컨텍스트가 뜨는 시점에 이미 update가 돌기 때문에, 이 테스트는 그 뒤의 스키마를 검사한다.
 */
@SpringBootTest
class SpotSchemaGuardTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final List<String> TEXT_COLUMNS =
            List.of("title", "addr1", "first_image", "lcls_systm1", "lcls_systm2", "lcls_systm3", "overview");

    @Test
    @DisplayName("엔티티를 매핑해도 긴 문자열 컬럼이 varchar로 좁혀지지 않는다")
    void textColumnsStayText() {
        for (String table : List.of("spot", "festival")) {
            for (String column : TEXT_COLUMNS) {
                String dataType = jdbcTemplate.queryForObject("""
                        SELECT data_type FROM information_schema.columns
                        WHERE table_name = ? AND column_name = ?
                        """, String.class, table, column);

                assertThat(dataType)
                        .describedAs("%s.%s", table, column)
                        .isEqualTo("text");
            }
        }
    }

    @Test
    @DisplayName("엔티티가 BaseTimeEntity를 상속하지 않아 감사 컬럼이 생기지 않는다")
    void noAuditColumnsAdded() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT table_name, column_name FROM information_schema.columns
                WHERE table_name IN ('spot', 'festival') AND column_name IN ('created_at', 'updated_at')
                """);

        assertThat(rows).isEmpty();
    }
}
