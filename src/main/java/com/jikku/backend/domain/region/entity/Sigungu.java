package com.jikku.backend.domain.region.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시군구 마스터(참조) 엔티티. (CLAUDE.md §region — 기준 데이터)
 * - 이미 DB에 적재된 읽기 전용 마스터라 setter/생성용 빌더를 두지 않는다.
 * - sigungu_cd는 행정 코드(예: 51110=춘천시)를 그대로 쓰는 '자연키'다.
 *   값을 우리가 직접 넣으므로 @GeneratedValue를 붙이지 않는다(자동증가 아님).
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 스펙상 기본 생성자 필요, 외부 생성은 막는다
@Table(name = "sigungu")
public class Sigungu {

    // 자연키(행정 코드)라 DB가 아니라 우리가 값을 지정 → @GeneratedValue 없음
    @Id
    @Column(name = "sigungu_cd")
    private Integer sigunguCd;

    // 광역 코드(강원=51)
    @Column(name = "area_cd", nullable = false)
    private Integer areaCd;

    // 시군구명(예: 춘천시)
    @Column(name = "sigungu_nm", length = 20, nullable = false)
    private String sigunguNm;

    // 광역명(예: 강원특별자치도)
    @Column(name = "area_nm", length = 20, nullable = false)
    private String areaNm;
}
