package com.jikku.backend.domain.member.exception;

import com.jikku.backend.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 회원·소셜 로그인 에러 코드. 코드 형식: {도메인}{HTTP상태}_{일련번호} */
@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    INVALID_KAKAO_TOKEN(HttpStatus.UNAUTHORIZED, "KAKAO401_1", "카카오 액세스 토큰이 유효하지 않습니다."),
    // 카카오 장애와 우리 서버 오류를 프론트가 구분할 수 있도록 502로 낸다.
    KAKAO_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "KAKAO502_1", "카카오 인증 서버 호출에 실패했습니다."),
    KAKAO_EMAIL_NOT_AGREED(HttpStatus.BAD_REQUEST, "KAKAO400_1", "카카오 계정의 이메일 제공에 동의해야 로그인할 수 있습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
