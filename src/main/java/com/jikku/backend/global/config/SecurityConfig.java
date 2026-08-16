package com.jikku.backend.global.config;

import com.jikku.backend.global.security.JwtAuthenticationFilter;
import com.jikku.backend.global.security.RestAccessDeniedHandler;
import com.jikku.backend.global.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 보안 필터체인 설정. 화이트리스트 외 요청은 유효한 JWT를 요구한다.
 */
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 인증 없이 허용할 경로 (auth는 도메인 컨트롤러라 /api/v1 프리픽스가 붙는다)
    private final String[] allowUris = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/v1/auth/**",
            "/health"          // Fly 헬스체크는 토큰을 못 붙인다. 인증을 걸면 401이라 머신이 죽은 것으로 판정된다
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CorsConfig의 CorsConfigurationSource 빈을 쓴다. 이걸 켜야 preflight(OPTIONS)가
                // 인가 규칙에 닿기 전에 CorsFilter에서 처리돼 401 없이 통과한다.
                .cors(Customizer.withDefaults())
                // 무상태 JSON API라 세션·CSRF·폼로그인·기본인증을 끈다
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        // 로그아웃은 지울 대상을 알아야 해서 인증이 필요하다. 아래 /auth/** 허용보다 먼저 와야 적용된다.
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").authenticated()
                        .requestMatchers(allowUris).permitAll()
                        .anyRequest().authenticated()
                )
                // 인증/인가 실패 응답도 공통 ApiResponse 포맷으로 통일
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
