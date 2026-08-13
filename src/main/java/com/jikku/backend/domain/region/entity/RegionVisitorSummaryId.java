package com.jikku.backend.domain.region.entity;

import java.io.Serializable;

/** region_visitor_summary의 복합 PK (sigungu_cd, month). */
public record RegionVisitorSummaryId(Integer sigunguCd, Short month) implements Serializable {

    // JPA가 리플렉션으로 인스턴스를 만들 때 필요하다
    public RegionVisitorSummaryId() {
        this(null, null);
    }
}
