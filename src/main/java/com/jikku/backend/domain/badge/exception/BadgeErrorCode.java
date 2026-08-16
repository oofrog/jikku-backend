package com.jikku.backend.domain.badge.exception;

import com.jikku.backend.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BadgeErrorCode implements BaseErrorCode {

  BADGE_NOT_FOUND(HttpStatus.NOT_FOUND, "BADGE404_1", "존재하지 않는 배지입니다."),
  MEMBER_BADGE_NOT_FOUND(HttpStatus.NOT_FOUND, "BADGE404_2", "획득한 배지 정보를 찾을 수 없습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
