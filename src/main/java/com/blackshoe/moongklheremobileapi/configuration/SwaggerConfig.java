package com.blackshoe.moongklheremobileapi.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI swagger(){
        return new OpenAPI().info(
                new io.swagger.v3.oas.models.info.Info()
                        .title("뭉클히어 모바일 API")
                        .description("뭉클히어 모바일 API 동적 명세서")
                        .version("v1.0.0")
        );
    }
}