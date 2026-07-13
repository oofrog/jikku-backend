package com.jikku.backend.domain.region.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시군구 마스터(참조) 엔티티. 이미 적재된 읽기 전용 마스터라 setter를 두지 않는다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sigungu")
public class Sigungu {

    // sigungu_cd는 행정 코드(예: 51110=춘천시)를 쓰는 자연키 → 값을 직접 넣으므로 @GeneratedValue 없음
    @Id
    @Column(name = "sigungu_cd")
    private Integer sigunguCd;

    @Column(name = "area_cd", nullable = false)
    private Integer areaCd;

    @Column(name = "sigungu_nm", length = 20, nullable = false)
    private String sigunguNm;

    @Column(name = "area_nm", length = 20, nullable = false)
    private String areaNm;
}
