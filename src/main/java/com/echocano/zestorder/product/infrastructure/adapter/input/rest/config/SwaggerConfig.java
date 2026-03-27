package com.echocano.zestorder.product.infrastructure.adapter.input.rest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Products API")
                        .version("1.0")
                        .description("API for managing products on ZestOrder. Built with Spring Boot 3 and Spring WebFlux."));
    }
}
