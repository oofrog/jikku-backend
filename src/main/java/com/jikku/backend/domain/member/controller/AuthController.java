package com.jikku.backend.domain.member.controller;

import com.jikku.backend.domain.member.dto.TokenResponse;
import com.jikku.backend.domain.member.service.AuthService;
import com.jikku.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 API. /auth/**는 SecurityConfig 화이트리스트라 토큰 없이 접근 가능하다.
 */
@Tag(name = "Auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "개발용 로그인",
            description = "카카오 없이 고정 테스트 회원의 Access 토큰을 발급한다. (개발 전용) "
                    + "X-Dev-Key 헤더에 보호키가 필요하며, 서버에 키가 설정돼 있지 않으면 항상 거부한다.")
    @PostMapping("/dev-login")
    public ApiResponse<TokenResponse> devLogin(
            @Parameter(description = "개발용 로그인 보호키", required = true)
            @RequestHeader(value = "X-Dev-Key", required = false) String devKey) {
        return ApiResponse.onSuccess(authService.devLogin(devKey));
    }
}
