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

    private final JdbcTemplate jdbcTemplate;

    public RegionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 강원 시군구코드 집합 (contains로 필터링하므로 Set)
    public Set<Integer> findAllSigunguCodes() {
        List<Integer> codes = jdbcTemplate.queryForList(
                "SELECT sigungu_cd FROM region", Integer.class);
        return new HashSet<>(codes);
    }
}