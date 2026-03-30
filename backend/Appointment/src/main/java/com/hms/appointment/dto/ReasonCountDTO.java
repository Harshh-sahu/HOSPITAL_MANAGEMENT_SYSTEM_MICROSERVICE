package com.hms.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ReasonCountDTO", description = "Reason-wise appointment count summary")
public class ReasonCountDTO {

    @Schema(description = "Consultation reason", example = "Fever")
    private String reason;
    @Schema(description = "Number of appointments for reason", example = "12")
    private Long count;

}
