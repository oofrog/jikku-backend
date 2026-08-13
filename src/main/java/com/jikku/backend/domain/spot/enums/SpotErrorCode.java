package com.jikku.backend.domain.spot.enums;

import com.jikku.backend.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 관광지·축제 조회 에러 코드. 코드 형식: {도메인}{HTTP상태}_{일련번호} */
@Getter
@RequiredArgsConstructor
public enum SpotErrorCode implements BaseErrorCode {

    SPOT_NOT_FOUND(HttpStatus.NOT_FOUND, "SPOT404_1", "존재하지 않는 관광지입니다."),
    FESTIVAL_NOT_FOUND(HttpStatus.NOT_FOUND, "FESTIVAL404_1", "존재하지 않는 축제입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
