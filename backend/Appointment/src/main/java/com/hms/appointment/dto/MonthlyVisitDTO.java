package com.hms.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "MonthlyVisitDTO", description = "Monthly appointment count summary")
public class MonthlyVisitDTO {
    @Schema(description = "Month label", example = "MARCH")
    private String month;
    @Schema(description = "Total appointments in month", example = "18")
    private Long count;
}
