package com.hms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "MonthlyRoleCountDTO", description = "Count of registrations for a month")
public class MonthlyRoleCountDTO {


    @Schema(description = "Month label", example = "MARCH")
    private String month;
    @Schema(description = "Registered users count", example = "14")
    private Long count;

}
