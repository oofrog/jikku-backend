package com.jikku.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 모든 비즈니스 REST API에 /api/v1 프리픽스를 붙인다. (API 버전 관리)
 * forBasePackage로 우리 도메인 컨트롤러에만 적용하는 이유: springdoc(/v3/api-docs, /swagger-ui)까지
 * 프리픽스되면 Swagger 문서가 깨지므로, 그쪽(org.springdoc.*)은 건드리지 않기 위해서다.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api/v1",
                HandlerTypePredicate.forBasePackage("com.jikku.backend.domain"));
    }
}
