package com.hms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ResponseDTO", description = "Generic response message")
public class ResponseDTO {

    @Schema(description = "Response message", example = "Account created.")
    String message;

    
}
