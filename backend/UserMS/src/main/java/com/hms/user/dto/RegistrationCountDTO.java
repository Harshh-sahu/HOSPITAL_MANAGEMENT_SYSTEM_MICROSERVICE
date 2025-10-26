package com.hms.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCountDTO {

    private List<MonthlyRoleCountDTO> doctorCounts;
    private List<MonthlyRoleCountDTO> patientCounts;
}
