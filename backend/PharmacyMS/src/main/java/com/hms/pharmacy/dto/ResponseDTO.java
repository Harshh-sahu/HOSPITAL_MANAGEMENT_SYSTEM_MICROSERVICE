package com.hms.pharmacy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ResponseDTO", description = "Generic success response")
public class ResponseDTO {
    @Schema(description = "Response message", example = "Medicine Updated !")
    private String message;
}
