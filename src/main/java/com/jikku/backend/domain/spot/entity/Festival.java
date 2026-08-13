package com.jikku.backend.domain.spot.entity;

import com.jikku.backend.domain.region.entity.Sigungu;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * TourAPI 축제(contentTypeId=15) 조회용 엔티티. 서빙 전용인 이유는 {@link Spot} 주석 참고.
 * 관광지와 컬럼이 겹치지만 상속으로 묶지 않는다 — 테이블이 분리돼 있고, 행사 기간이 축제에만 있다.
 */
@Entity
@Getter
@Table(name = "festival")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Festival {

    @Id
    @Column(name = "content_id")
    private Long contentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sigungu_cd", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Sigungu sigungu;

    @Column(name = "title", nullable = false, columnDefinition = "text")
    private String title;

    @Column(name = "addr1", columnDefinition = "text")
    private String addr1;

    @Column(name = "map_x", precision = 16, scale = 12)
    private BigDecimal mapX;

    @Column(name = "map_y", precision = 16, scale = 12)
    private BigDecimal mapY;

    @Column(name = "first_image", columnDefinition = "text")
    private String firstImage;

    @Column(name = "lcls_systm1", columnDefinition = "text")
    private String lclsSystm1;

    @Column(name = "lcls_systm2", columnDefinition = "text")
    private String lclsSystm2;

    @Column(name = "lcls_systm3", columnDefinition = "text")
    private String lclsSystm3;

    @Column(name = "overview", columnDefinition = "text")
    private String overview;

    // 행사 기간은 searchFestival2에만 있어 areaBasedList2로는 못 채운다(CLAUDE.md §4.1)
    @Column(name = "event_start_date", nullable = false)
    private LocalDate eventStartDate;

    @Column(name = "event_end_date")
    private LocalDate eventEndDate;

    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;
}
