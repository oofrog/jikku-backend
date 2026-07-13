package com.jikku.backend.domain.member.controller;

import com.jikku.backend.domain.member.dto.MemberResponse;
import com.jikku.backend.domain.member.service.MemberService;
import com.jikku.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "member", description = "마이페이지 API")
@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;

    // 현재 로그인 회원 조회. memberId는 JwtAuthenticationFilter가 SecurityContext에 심어둔 값을 꺼내 쓴다. (헤더 재파싱 불필요)
    @Operation(summary = "마이페이지 조회", description = "현재 로그인한 회원의 정보를 조회한다.")
    @GetMapping("/my-page")
    public ApiResponse<MemberResponse> myPage(@AuthenticationPrincipal Long memberId) {
        return ApiResponse.onSuccess(memberService.getMyPage(memberId));
    }
}
