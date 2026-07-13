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

    // Lombok @Getter는 boolean 필드에 isSuccess()가 아닌 getSuccess()류를 만들 수 있어, JSON 키 고정을 위해 직접 정의
    @JsonProperty("isSuccess")
    public boolean isSuccess() {
        return isSuccess;
    }

    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(true, "OK", "요청이 성공했습니다.", result);
    }

    public static <T> ApiResponse<T> onSuccess() {
        return onSuccess(null);
    }

    public static ApiResponse<Void> onFailure(BaseErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static ApiResponse<Void> onFailure(BaseErrorCode errorCode, String message) {
        return new ApiResponse<>(false, errorCode.getCode(), message, null);
    }
}