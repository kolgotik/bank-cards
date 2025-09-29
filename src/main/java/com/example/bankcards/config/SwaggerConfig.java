package com.example.bankcards.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        Info info = new Info();
        info.setTitle("Bank Cards API");
        info.setVersion("1.0");
        return GroupedOpenApi.builder()
                .addOpenApiCustomizer(customizer -> customizer.setInfo(info))
                .displayName("Bank Cards API")
                .group("public")
                .pathsToMatch("/**")
                .build();
    }
}
