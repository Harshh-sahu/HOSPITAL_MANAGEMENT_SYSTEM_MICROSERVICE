package com.hms.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Appointment lifecycle status", allowableValues = {"SCHEDULED", "CANCELLED", "COMPLETED"})
public enum Status {

    SCHEDULED,CANCELLED,COMPLETED
}
