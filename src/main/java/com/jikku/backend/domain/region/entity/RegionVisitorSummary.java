package com.jikku.backend.domain.region.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시군구 × 월별 방문자 집계. TourAPI 빅데이터를 적재·집계해둔 읽기 전용 테이블이라 setter를 두지 않는다.
 * tourist_sum·local_sum은 응답에 쓰지 않으므로 일부러 매핑하지 않는다 —
 * numeric 컬럼을 매핑하면 ddl-auto=update가 정밀도를 바꿀 수 있어 건드리지 않는 편이 안전하다.
 */
@Entity
@Getter
@Table(name = "region_visitor_summary")
@IdClass(RegionVisitorSummaryId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegionVisitorSummary {

    @Id
    @Column(name = "sigungu_cd")
    private Integer sigunguCd;

    @Id
    @Column(name = "month")
    private Short month;

    // 이미 DB에 FK가 있어 Hibernate가 같은 제약을 하나 더 만들지 않도록 막는다.
    // sigungu_cd가 PK 일부라 같은 컬럼을 두 번 매핑하게 되므로 이쪽은 읽기 전용으로 둔다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sigungu_cd", insertable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Sigungu sigungu;

    // 그 달의 방문자 오름차순 순위(1 = 가장 적음). 적재 시 계산돼 있어 조회 시점 계산이 없다.
    @Column(name = "rank_asc")
    private Integer rankAsc;
}
