package com.jikku.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    // Authorize 버튼에 등록할 인증 스킴 이름 (아래 SecurityRequirement와 이름이 일치해야 연결됨)
    private static final String JWT_SCHEME = "JWT";

    @Bean
    public OpenAPI jikkuOpenAPI() {
        // Bearer(JWT) 스킴 정의 → Swagger 우측 상단에 🔒 Authorize 버튼 생성, 넣은 토큰을 자동으로 Authorization 헤더에 첨부
        SecurityScheme jwtScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("Ji-kku(지꾸) API")
                        .description("지도 꾸미기 웹앱 API 문서")
                        .version("v0.0.1"))
                .components(new Components().addSecuritySchemes(JWT_SCHEME, jwtScheme))
                // 모든 API에 기본으로 이 스킴을 적용 (Authorize 한 번이면 전 요청에 토큰 첨부)
                .addSecurityItem(new SecurityRequirement().addList(JWT_SCHEME));
    }
}
