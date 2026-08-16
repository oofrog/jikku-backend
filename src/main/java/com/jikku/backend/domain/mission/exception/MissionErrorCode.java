package com.jikku.backend.domain.mission.exception;

import com.jikku.backend.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 미션 도메인 에러 코드. 코드 형식: {도메인}{HTTP상태}_{일련번호} */
@Getter
@RequiredArgsConstructor
public enum MissionErrorCode implements BaseErrorCode {

  MISSION_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "MISSION400_1", "인증 가능 거리 밖입니다."),
  MISSION_COORDINATE_NOT_FOUND(HttpStatus.NOT_FOUND, "MISSION404_1", "관광지 좌표 정보가 존재하지 않습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
