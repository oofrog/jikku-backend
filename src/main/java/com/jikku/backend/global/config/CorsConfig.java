package com.jikku.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * 브라우저 CORS 허용 규칙. 프론트(PWA)가 백엔드와 다른 오리진에서 돌기 때문에 필요하다.
 * 이게 없으면 preflight(OPTIONS)가 시큐리티 필터에 막혀 로그인부터 전부 실패한다.
 */
@Configuration
public class CorsConfig {

    private final List<String> allowedOrigins;

    public CorsConfig(@Value("${cors.allowed-origins}") List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Authorization·X-Dev-Key 등 요청 헤더를 일일이 나열하지 않는다. 헤더는 오리진과 달리
        // 그 자체로 권한이 아니고, 허용 오리진을 이미 좁혀뒀다.
        config.setAllowedHeaders(List.of("*"));
        // 토큰을 쿠키가 아니라 Authorization 헤더로 주고받아 credentials가 필요 없다.
        // 켜는 순간 오리진 와일드카드가 금지되고 CSRF 고려가 따라붙으므로 끈 채로 둔다.
        config.setAllowCredentials(false);
        // preflight 결과를 1시간 캐시해 요청마다 OPTIONS가 한 번씩 더 나가는 것을 막는다.
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
