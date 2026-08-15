package com.jikku.backend.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 스프링 컨텍스트 없이 CORS 규칙 자체만 검증한다. 오리진 목록을 잘못 넣으면
 * 배포 후 프론트에서 전 API가 막히는데, 그때는 원인이 잘 안 보인다.
 */
class CorsConfigTest {

    private CorsConfiguration configFor(String... origins) {
        return (CorsConfiguration) new CorsConfig(List.of(origins))
                .corsConfigurationSource()
                .getCorsConfiguration(new org.springframework.mock.web.MockHttpServletRequest("GET", "/api/v1/spots"));
    }

    @Test
    @DisplayName("허용 목록에 있는 오리진은 통과한다")
    void allowsListedOrigin() {
        CorsConfiguration config = configFor("http://localhost:3000", "https://jikku.app");

        assertThat(config.checkOrigin("http://localhost:3000")).isEqualTo("http://localhost:3000");
        assertThat(config.checkOrigin("https://jikku.app")).isEqualTo("https://jikku.app");
    }

    @Test
    @DisplayName("허용 목록에 없는 오리진은 거부한다")
    void rejectsUnlistedOrigin() {
        CorsConfiguration config = configFor("http://localhost:3000");

        assertThat(config.checkOrigin("https://evil.example")).isNull();
        // 포트가 다르면 브라우저는 다른 오리진으로 본다. 5173을 빼먹으면 Vite에서 막힌다.
        assertThat(config.checkOrigin("http://localhost:5173")).isNull();
    }

    @Test
    @DisplayName("preflight가 쓰는 메서드와 헤더를 허용한다")
    void allowsPreflightMethodsAndHeaders() {
        CorsConfiguration config = configFor("http://localhost:3000");

        assertThat(config.checkHttpMethod(org.springframework.http.HttpMethod.DELETE)).isNotNull();
        assertThat(config.checkHeaders(List.of("Authorization", "Content-Type", "X-Dev-Key"))).isNotNull();
    }
}
