package com.jikku.backend.global.common;

import com.jikku.backend.global.error.ErrorCode;
import lombok.Getter;

/**
 * 모든 API 응답을 감싸는 공통 포맷.
 * isSuccess / code / message / data 형태로 통일한다.
 */
@Getter
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

    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(true, "OK", "요청이 성공했습니다.", result);
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    } // 반환값 없을때 사용

    public static <T> ApiResponse<T> error(ErrorCode errorCode, T result) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), result);
    }

    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }
}
