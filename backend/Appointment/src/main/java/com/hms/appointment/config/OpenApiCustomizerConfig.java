package com.hms.appointment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiCustomizerConfig {

    @Bean
    public OpenApiCustomizer globalErrorResponsesCustomizer() {
        return openApi -> {
            registerErrorResponseComponents(openApi);
            applyDefaultErrorResponses(openApi);
        };
    }

    private void registerErrorResponseComponents(OpenAPI openApi) {
        if (openApi.getComponents() == null) {
            openApi.setComponents(new io.swagger.v3.oas.models.Components());
        }

        openApi.getComponents().addSchemas("ErrorInfo", new ObjectSchema()
                .addProperty("errorMessage", new io.swagger.v3.oas.models.media.StringSchema().example("Appointment not found"))
                .addProperty("errorCode", new io.swagger.v3.oas.models.media.IntegerSchema().example(500))
                .addProperty("timeStamp", new io.swagger.v3.oas.models.media.StringSchema().format("date-time").example("2026-03-30T17:00:00"))
                .description("Standard API error payload"));

        openApi.getComponents().addResponses("BadRequestResponse", new ApiResponse()
                .description("Invalid request or validation failure")
                .content(new io.swagger.v3.oas.models.media.Content().addMediaType("application/json",
                        new io.swagger.v3.oas.models.media.MediaType().schema(new io.swagger.v3.oas.models.media.Schema<>().$ref("#/components/schemas/ErrorInfo")))));

        openApi.getComponents().addResponses("InternalErrorResponse", new ApiResponse()
                .description("Business or server error")
                .content(new io.swagger.v3.oas.models.media.Content().addMediaType("application/json",
                        new io.swagger.v3.oas.models.media.MediaType().schema(new io.swagger.v3.oas.models.media.Schema<>().$ref("#/components/schemas/ErrorInfo")))));
    }

    private void applyDefaultErrorResponses(OpenAPI openApi) {
        if (openApi.getPaths() == null) {
            return;
        }

        openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
            ApiResponses responses = operation.getResponses();
            if (responses == null) {
                responses = new ApiResponses();
                operation.setResponses(responses);
            }
            responses.putIfAbsent("400", new ApiResponse().$ref("#/components/responses/BadRequestResponse"));
            responses.putIfAbsent("500", new ApiResponse().$ref("#/components/responses/InternalErrorResponse"));
        }));
    }
}

