package com.hms.MediaMS.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ErrorInfo", description = "Standard API error response payload")
public class ErrorInfo {

    @Schema(description = "Error message", example = "File not found")
    private String errorMessage;

    @Schema(description = "HTTP status code", example = "404")
    private Integer errorCode;

    @Schema(description = "Error timestamp", example = "2026-03-30T17:00:00")
    private LocalDateTime timeStamp;
}

