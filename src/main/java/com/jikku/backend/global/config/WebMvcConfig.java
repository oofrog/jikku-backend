package com.jikku.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 도메인 REST API에만 /api/v1 프리픽스를 붙인다.
 * forBasePackage로 우리 컨트롤러에만 한정하는 이유: springdoc(/v3/api-docs, /swagger-ui)까지
 * 프리픽스되면 Swagger 문서가 깨지기 때문이다.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api/v1",
                HandlerTypePredicate.forBasePackage("com.jikku.backend.domain"));
    }
}
