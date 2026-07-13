package com.jikku.backend.domain.region.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 읍면동 마스터(참조) 엔티티. 이미 적재된 읽기 전용 마스터라 setter를 두지 않는다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "emd", uniqueConstraints = {
        @UniqueConstraint(name = "uk_emd_sigungu_nm", columnNames = {"sigungu_cd", "emd_nm"})
})
public class Emd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emd_id")
    private Long emdId;

    @Column(name = "emd_nm", length = 20, nullable = false)
    private String emdNm;

    // N+1 방지를 위해 LAZY 로딩
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sigungu_cd", nullable = false)
    private Sigungu sigungu;
}
