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
 * 읍면동 마스터(참조) 엔티티. (CLAUDE.md §region — 기준 데이터)
 * - 이미 DB에 적재된 읽기 전용 마스터라 setter/생성용 빌더를 두지 않는다.
 * - emd_id는 DB가 채번(GENERATED ALWAYS AS IDENTITY)하므로 IDENTITY 전략을 쓴다.
 * - (sigungu_cd, emd_nm) 복합 유니크: 같은 시군구 안에서 동명 읍면동 중복 방지.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 스펙상 기본 생성자 필요, 외부 생성은 막는다
@Table(name = "emd", uniqueConstraints = {
        @UniqueConstraint(name = "uk_emd_sigungu_nm", columnNames = {"sigungu_cd", "emd_nm"})
})
public class Emd {

    // DB가 채번하는 PK → IDENTITY 전략
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emd_id")
    private Long emdId;

    // 읍면동명(예: 봉평면)
    @Column(name = "emd_nm", length = 20, nullable = false)
    private String emdNm;

    // 소속 시군구. N+1 방지를 위해 반드시 LAZY 로딩.
    // FK 컬럼은 sigungu_cd 하나뿐이라 @JoinColumn으로 매핑(별도 sigunguCd 필드는 두지 않음).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sigungu_cd", nullable = false)
    private Sigungu sigungu;
}
