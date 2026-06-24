package com.jikku.backend.domain.tourApi.repository;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class RegionRepository {

    private final JdbcTemplate jdbcTemplate;  // 스프링이 DataSource 보고 자동으로 만들어 주입

    public RegionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // region 테이블의 시군구코드 18개를 Set 으로 읽어온다.
    // Set 인 이유: 뒤에서 'contains(코드)' 로 필터링할 때 빠르고 의도가 명확해서.
    public Set<Integer> findAllSigunguCodes() {
        List<Integer> codes = jdbcTemplate.queryForList(
                "SELECT sigungu_cd FROM region", Integer.class);
        return new HashSet<>(codes);
    }
}