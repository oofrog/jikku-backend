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

    // addSecuritySchemes와 addSecurityItem에서 같은 이름을 참조해야 연결된다
    private static final String JWT_SCHEME = "JWT";

    @Bean
    public OpenAPI jikkuOpenAPI() {
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
                .addSecurityItem(new SecurityRequirement().addList(JWT_SCHEME));
    }
}
