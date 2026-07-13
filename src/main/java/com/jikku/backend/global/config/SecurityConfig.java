package com.jikku.backend.global.config;

import com.jikku.backend.global.security.JwtAuthenticationFilter;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 보안 필터체인 설정.
 * JWT 인증 필터를 스프링 시큐리티 체인 앞단에 꽂아, 화이트리스트 외 요청은 유효한 토큰을 요구한다. (CLAUDE.md §5.1)
 * 로그인 토큰 발급은 dev-login(임시) → 이후 카카오로 대체 예정.
 */
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 인증 없이 항상 허용할 경로 (Phase 2에서 나머지를 막아도 열려 있어야 하는 것들)
    private final String[] allowUris = {
            // Swagger (프리픽스 대상 아님 → 경로 그대로)
            "/swagger-ui/**",
            "/v3/api-docs/**",

            // 로그인 (도메인 컨트롤러라 /api/v1 프리픽스가 붙음)
            "/api/v1/auth/**"
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
                        // 화이트리스트 외 모든 요청은 유효한 JWT 필요
                        .anyRequest().authenticated()
                )
                // 인증/인가 실패 응답을 공통 포맷(ApiResponse)으로 통일
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                // 스프링 기본 인증 필터 자리 앞에 우리 JWT 필터를 배치 (토큰으로 SecurityContext를 먼저 채움)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
