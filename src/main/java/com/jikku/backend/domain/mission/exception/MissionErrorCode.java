package com.jikku.backend.domain.mission.exception;

import com.jikku.backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MissionErrorCode implements BaseErrorCode {
  MISSION_SPOT_NOT_FOUND(HttpStatus.NOT_FOUND, "MISSION_001", "존재하지 않는 미션 관광지입니다."),
  MISSION_ALREADY_COMPLETED(HttpStatus.CONFLICT, "MISSION_002", "이미 방문 인증한 미션입니다."),
  MISSION_TARGET_SPOT_NOT_FOUND(HttpStatus.NOT_FOUND, "MISSION_003", "관광지 정보를 찾을 수 없습니다."),
  MISSION_COORDINATE_NOT_FOUND(HttpStatus.NOT_FOUND, "MISSION_004", "미션 관광지 좌표를 찾을 수 없습니다."),
  MISSION_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "MISSION_005", "방문 가능한 범위를 벗어났습니다."),
  MISSION_SIGUNGU_NOT_FOUND(HttpStatus.NOT_FOUND, "MISSION_006", "존재하지 않는 시군구 코드입니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
