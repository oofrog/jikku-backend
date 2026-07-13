package com.jikku.backend.global.security;

import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.ObjectMapper;
import com.jikku.backend.global.apiPayload.ApiResponse;
import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 인증은 됐지만 권한이 없는 요청에 403 응답을 만든다.
 * 스프링 기본 응답 대신 앱 공통 포맷(ApiResponse)으로 통일하기 위해 직접 구현한다. (CLAUDE.md §3)
 */
@Component
@RequiredArgsConstructor
@NullMarked
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        writeErrorResponse(response);
    }

    private void writeErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(GeneralErrorCode.ACCESS_DENIED.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(),
                ApiResponse.onFailure(GeneralErrorCode.ACCESS_DENIED));
    }
}
