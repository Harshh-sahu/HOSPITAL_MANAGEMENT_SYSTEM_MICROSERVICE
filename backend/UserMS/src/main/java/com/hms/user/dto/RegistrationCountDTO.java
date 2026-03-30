package com.hms.user.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "RegistrationCountDTO", description = "Monthly role-wise registration counts")
public class RegistrationCountDTO {

    @ArraySchema(schema = @Schema(implementation = MonthlyRoleCountDTO.class), arraySchema = @Schema(description = "Monthly doctor registration counts"))
    private List<MonthlyRoleCountDTO> doctorCounts;
    @ArraySchema(schema = @Schema(implementation = MonthlyRoleCountDTO.class), arraySchema = @Schema(description = "Monthly patient registration counts"))
    private List<MonthlyRoleCountDTO> patientCounts;
}
