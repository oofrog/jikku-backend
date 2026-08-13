package com.jikku.backend.domain.tourApi.dto;

/**
 * 법정동 코드를 갖는 TourAPI 목록 항목(관광지·축제)의 공통 부분.
 * 시군구 코드 결합 규칙이 두 DTO에 각각 있으면 한쪽만 고쳐질 수 있어 한곳에 모은다.
 */
public interface HasLDongCode {

    String contentid();

    String lDongRegnCd();

    String lDongSignguCd();

    /** lDongRegnCd(51) + lDongSignguCd(720) = 51720 으로 sigungu.sigungu_cd와 그대로 맞는다. */
    default int sigunguCd() {
        return Integer.parseInt(lDongRegnCd() + lDongSignguCd());
    }

    /**
     * 적재 대상인지 확인한다. 숫자가 아닌 값이 섞이면 저장 시점에 형변환이 터지면서
     * 배치 전체가 롤백되므로, 배치에 넣기 전에 걸러야 한다.
     */
    default boolean hasNumericCodes() {
        return isDigits(contentid()) && isDigits(lDongRegnCd()) && isDigits(lDongSignguCd());
    }

    static boolean isDigits(String value) {
        return value != null && !value.isBlank() && value.chars().allMatch(Character::isDigit);
    }
}
