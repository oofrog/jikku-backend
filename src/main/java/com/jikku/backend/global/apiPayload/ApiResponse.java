package com.jikku.backend.global.apiPayload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.jikku.backend.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;

/**
 * 모든 API 응답을 감싸는 공통 포맷.
 * 필드 순서는 isSuccess → code → message → result 로 고정한다.
 */
@Getter
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

    private final boolean isSuccess;
    private final String code;
    private final String message;
    private final T result;

    private ApiResponse(boolean isSuccess, String code, String message, T result) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.result = result;
    }

    /** JSON 키를 isSuccess 로 고정하기 위해 getter 를 직접 정의한다. */
    @JsonProperty("isSuccess")
    public boolean isSuccess() {
        return isSuccess;
    }

    // 성공 (기본 코드)
    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(true, "OK", "요청이 성공했습니다.", result);
    }

    // 성공 (result 없음)
    public static <T> ApiResponse<T> onSuccess() {
        return onSuccess(null);
    }

    // 실패 (전역 예외 핸들러에서 사용)
    public static ApiResponse<Void> onFailure(BaseErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }

    // 실패 (검증 메시지 등 커스텀 메시지를 message 에 담을 때)
    public static ApiResponse<Void> onFailure(BaseErrorCode errorCode, String message) {
        return new ApiResponse<>(false, errorCode.getCode(), message, null);
    }
}