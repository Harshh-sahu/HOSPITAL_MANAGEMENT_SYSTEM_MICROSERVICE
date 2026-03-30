package com.hms.pharmacy.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        name = "X-Secret-Key",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-Secret-Key",
        description = "Provide SECRET as header value to access protected endpoints"
)
public class OpenApiConfig {

    private static final String SECRET_HEADER_SCHEME = "X-Secret-Key";

    @Bean
    public OpenAPI pharmacyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pharmacy Service API")
                        .version("v1")
                        .description("APIs for medicine catalog, inventory, and sales")
                        .contact(new Contact().name("HMS Team").email("support@hms.local"))
                        .license(new License().name("Internal Use")))
                .servers(List.of(
                        new Server().url("http://localhost:9300").description("Local Pharmacy service"),
                        new Server().url("http://localhost:8765").description("Gateway (if route is configured)")
                ))
                .components(new Components().addSecuritySchemes(SECRET_HEADER_SCHEME,
                        new io.swagger.v3.oas.models.security.SecurityScheme()
                                .type(Type.APIKEY)
                                .in(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER)
                                .name(SECRET_HEADER_SCHEME)
                                .description("Provide SECRET as header value for protected endpoints")))
                .addSecurityItem(new SecurityRequirement().addList(SECRET_HEADER_SCHEME))
                .externalDocs(new ExternalDocumentation()
                        .description("Pharmacy service OpenAPI document")
                        .url("/v3/api-docs"));
    }
}

