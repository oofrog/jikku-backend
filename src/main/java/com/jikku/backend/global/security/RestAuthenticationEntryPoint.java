package com.jikku.backend.global.security;

import tools.jackson.databind.ObjectMapper;
import com.jikku.backend.global.apiPayload.ApiResponse;
import com.jikku.backend.global.apiPayload.code.BaseErrorCode;
import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 인증되지 않은 요청이 보호된 자원에 접근할 때의 401 응답을 공통 ApiResponse 포맷으로 내린다.
 */
@Component
@RequiredArgsConstructor
@NullMarked
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /** JwtAuthenticationFilter가 실패 사유(BaseErrorCode)를 담아두는 요청 속성 키. */
    public static final String AUTH_ERROR_CODE = "authErrorCode";

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        writeErrorResponse(response, resolveErrorCode(request));
    }

    // 필터가 사유를 남기지 않았으면 토큰이 아예 없었던 경우다
    private BaseErrorCode resolveErrorCode(HttpServletRequest request) {
        return request.getAttribute(AUTH_ERROR_CODE) instanceof BaseErrorCode errorCode
                ? errorCode
                : GeneralErrorCode.UNAUTHORIZED;
    }

    private void writeErrorResponse(HttpServletResponse response, BaseErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), ApiResponse.onFailure(errorCode));
    }
}
