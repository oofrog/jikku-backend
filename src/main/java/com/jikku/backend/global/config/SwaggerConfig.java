package com.jikku.backend.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI jikkuOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ji-kku(지꾸) API")
                        .description("지도 꾸미기 웹앱 API 문서")
                        .version("v0.0.1"));
    }
}
