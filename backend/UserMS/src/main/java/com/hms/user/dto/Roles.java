package com.hms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Available user roles", allowableValues = {"PATIENT", "DOCTOR", "ADMIN"})
public enum Roles {
    PATIENT,
    DOCTOR,
    ADMIN
    
}
