package com.jikku.backend.domain.member.exception;

import com.jikku.backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {
  INVALID_KAKAO_TOKEN(HttpStatus.UNAUTHORIZED, "KAKAO401_1", "카카오 액세스 토큰이 유효하지 않습니다."),
  INVALID_KAKAO_CODE(HttpStatus.UNAUTHORIZED, "KAKAO401_2", "카카오 인가 코드가 유효하지 않습니다."),
  KAKAO_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "KAKAO502_1", "카카오 인증 서버 호출에 실패했습니다."),
  KAKAO_EMAIL_NOT_AGREED(HttpStatus.BAD_REQUEST, "KAKAO400_1", "카카오 계정의 이메일 제공에 동의해야 로그인할 수 있습니다."),
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_001", "존재하지 않는 사용자입니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
