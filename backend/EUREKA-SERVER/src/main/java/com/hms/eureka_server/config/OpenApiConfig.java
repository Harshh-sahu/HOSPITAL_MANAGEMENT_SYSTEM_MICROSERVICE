package com.hms.eureka_server.config;

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
    public OpenAPI eurekaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Eureka Server API")
                        .version("v1")
                        .description("Service registry and discovery dashboard endpoints")
                        .contact(new Contact().name("HMS Team").email("support@hms.local"))
                        .license(new License().name("Internal Use")))
                .servers(List.of(
                        new Server().url("http://localhost:8761").description("Local Eureka server")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Eureka Dashboard")
                        .url("/"));
    }
}

