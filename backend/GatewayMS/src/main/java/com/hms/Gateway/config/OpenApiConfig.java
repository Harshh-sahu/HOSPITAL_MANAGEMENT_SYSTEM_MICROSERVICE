package com.hms.Gateway.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gateway Service API")
                        .version("v1")
                        .description("API Gateway routes and token-filter behavior")
                        .contact(new Contact().name("HMS Team").email("support@hms.local"))
                        .license(new License().name("Internal Use")))
                .servers(List.of(
                        new Server().url("http://localhost:9000").description("Local Gateway service")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Gateway route configuration")
                        .url("/v3/api-docs"));
    }
}

