package com.EmailService.EmailServiceMS.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowUpReminderEvent {
    private Long recordId;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private Long doctorId;
    private String doctorName;
    private LocalDate followUpDate;
    private String diagnosis;
}
