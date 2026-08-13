package com.jikku.backend.global.security;

/**
 * Access 토큰 검증 결과.
 * 클라이언트가 "재발급하면 되는 상황(EXPIRED)"과 "다시 로그인해야 하는 상황(INVALID)"을 구분해야 해서
 * 유효/무효 두 갈래로는 부족하다.
 */
public enum AccessTokenStatus {
    VALID,
    EXPIRED,
    INVALID
}
