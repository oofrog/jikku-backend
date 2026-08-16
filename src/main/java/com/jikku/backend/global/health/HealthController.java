package com.jikku.backend.global.health;

import com.jikku.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 서버 생존 확인용. Fly.io의 HTTP 헬스체크가 이 경로를 본다.
 *
 * <p>DB 등 외부 의존은 일부러 확인하지 않는다. 여기에 Supabase 조회를 넣으면 DB가 잠깐 흔들릴 때
 * Fly가 머신을 죽은 것으로 보고 재시작하거나 배포를 실패시킨다. 짧은 DB 장애로 서버 전체를 내리는
 * 쪽이 더 나쁘다. 의존성 상태가 필요해지면 별도 경로로 분리한다.
 *
 * <p>domain이 아니라 global에 두어 /api/v1 프리픽스를 피한다(WebMvcConfig 참고).
 * 인프라용 경로라 API 버전을 따라다닐 이유가 없다.
 */
@Tag(name = "Health", description = "서버 상태 확인")
@RestController
public class HealthController {

    @Operation(summary = "헬스체크", description = "서버가 요청을 처리할 수 있으면 200을 반환한다.")
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.onSuccess("UP");
    }
}
