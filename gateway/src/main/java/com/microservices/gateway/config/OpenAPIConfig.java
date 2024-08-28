package com.microservices.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@OpenAPIDefinition
@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI gatewayServiceAPI() {
        return new OpenAPI()
                .info(new Info().title("Gateway Service API")
                        .description("This is the REST API for Gateway Service")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("You can refer to the Gateway Service Wiki Documentation")
                        .url(""));
    }
}
