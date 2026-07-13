package com.jikku.backend.global.config;

import com.jikku.backend.global.security.RestAccessDeniedHandler;
import com.jikku.backend.global.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 보안 필터체인 설정 (Phase 1 뼈대).
 * 지금은 JWT 필터가 없어 모든 요청을 permitAll 로 열어둔다. (개발 중 tourApi·Swagger 등이 막히지 않도록)
 * JWT 인증 필터·카카오 로그인은 Member 도메인과 함께 Phase 2에서 도입하며, 그때 anyRequest 를 authenticated 로 전환한다. (CLAUDE.md §5.1)
 */
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    // 인증 없이 항상 허용할 경로 (Phase 2에서 나머지를 막아도 열려 있어야 하는 것들)
    private final String[] allowUris = {
            // Swagger
            "/swagger-ui/**",
            "/v3/api-docs/**",

            // 로그인 (카카오 code 교환 등)
            "/auth/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // JSON API 서버라 세션·CSRF·폼로그인·기본인증을 쓰지 않는다 (인증은 JWT 예정)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(allowUris).permitAll()
                        // Phase 1: JWT 필터가 없어 나머지도 전부 허용. Phase 2에서 authenticated 로 교체.
                        .anyRequest().permitAll()
                )
                // 인증/인가 실패 응답을 공통 포맷(ApiResponse)으로 통일
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }
}
