package com.EmailService.EmailServiceMS.kafka;

import com.EmailService.EmailServiceMS.event.AppointmentCancelledEvent;
import com.EmailService.EmailServiceMS.event.AppointmentCreatedEvent;
import com.EmailService.EmailServiceMS.event.AppointmentReminderEvent;
import com.EmailService.EmailServiceMS.event.DoctorOnboardedEvent;
import com.EmailService.EmailServiceMS.event.FollowUpReminderEvent;
import com.EmailService.EmailServiceMS.event.LowStockAlertEvent;
import com.EmailService.EmailServiceMS.event.PatientRegisteredEvent;
import com.EmailService.EmailServiceMS.event.PrescriptionCreatedEvent;
import com.EmailService.EmailServiceMS.event.ReportCreatedEvent;
import com.EmailService.EmailServiceMS.event.SaleCreatedEvent;
import com.EmailService.EmailServiceMS.event.UserLoginEvent;
import com.EmailService.EmailServiceMS.event.UserRegisteredEvent;
import com.EmailService.EmailServiceMS.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmailKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmailKafkaConsumer.class);

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public EmailKafkaConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${hms.kafka.topic.user-registered}", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserRegistered(String payload) {
        try {
            UserRegisteredEvent event = objectMapper.readValue(payload, UserRegisteredEvent.class);
            emailService.sendWelcomeEmail(event);
        } catch (Exception e) {
            log.error("Failed to process user-registered event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${hms.kafka.topic.user-login}", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserLogin(String payload) {
        try {
            UserLoginEvent event = objectMapper.readValue(payload, UserLoginEvent.class);
            emailService.sendLoginAlertEmail(event);
        } catch (Exception e) {
            log.error("Failed to process user-login event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${hms.kafka.topic.appointment-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void onAppointmentCreated(String payload) {
        try {
            AppointmentCreatedEvent event = objectMapper.readValue(payload, AppointmentCreatedEvent.class);
            emailService.sendAppointmentEmail(event);
        } catch (Exception e) {
            log.error("Failed to process appointment-created event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${hms.kafka.topic.prescription-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void onPrescriptionCreated(String payload) {
        try {
            PrescriptionCreatedEvent event = objectMapper.readValue(payload, PrescriptionCreatedEvent.class);
            emailService.sendPrescriptionEmail(event);
        } catch (Exception e) {
            log.error("Failed to process prescription-created event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${hms.kafka.topic.report-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void onReportCreated(String payload) {
        try {
            ReportCreatedEvent event = objectMapper.readValue(payload, ReportCreatedEvent.class);
            emailService.sendMedicalReportEmail(event);
        } catch (Exception e) {
            log.error("Failed to process report-created event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${hms.kafka.topic.sale-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void onSaleCreated(String payload) {
        try {
            SaleCreatedEvent event = objectMapper.readValue(payload, SaleCreatedEvent.class);
            emailService.sendInvoiceEmail(event);
        } catch (Exception e) {
            log.error("Failed to process sale-created event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${hms.kafka.topic.doctor-onboarded}", groupId = "${spring.kafka.consumer.group-id}")
    public void onDoctorOnboarded(String payload) {
        try {
            DoctorOnboardedEvent event = objectMapper.readValue(payload, DoctorOnboardedEvent.class);
            emailService.sendOnboardingEmail(event);
        } catch (Exception e) {
            log.error("Failed to process doctor-onboarded event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${hms.kafka.topic.appointment-reminder}", groupId = "${spring.kafka.consumer.group-id}")
    public void onAppointmentReminder(String payload) {
        try {
            AppointmentReminderEvent event = objectMapper.readValue(payload, AppointmentReminderEvent.class);
            emailService.sendAppointmentReminderEmail(event);
        } catch (Exception e) {
            log.error("Failed to process appointment-reminder event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${hms.kafka.topic.followup-reminder}", groupId = "${spring.kafka.consumer.group-id}")
    public void onFollowUpReminder(String payload) {
        try {
            FollowUpReminderEvent event = objectMapper.readValue(payload, FollowUpReminderEvent.class);
            emailService.sendFollowUpReminderEmail(event);
        } catch (Exception e) {
            log.error("Failed to process followup-reminder event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${hms.kafka.topic.appointment-cancelled}", groupId = "${spring.kafka.consumer.group-id}")
    public void onAppointmentCancelled(String payload) {
        try {
            AppointmentCancelledEvent event = objectMapper.readValue(payload, AppointmentCancelledEvent.class);
            emailService.sendAppointmentCancelledEmail(event);
        } catch (Exception e) {
            log.error("Failed to process appointment-cancelled event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${hms.kafka.topic.low-stock-alert}", groupId = "${spring.kafka.consumer.group-id}")
    public void onLowStockAlert(String payload) {
        try {
            LowStockAlertEvent event = objectMapper.readValue(payload, LowStockAlertEvent.class);
            emailService.sendLowStockAlertEmail(event);
        } catch (Exception e) {
            log.error("Failed to process low-stock-alert event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${hms.kafka.topic.patient-registered}", groupId = "${spring.kafka.consumer.group-id}")
    public void onPatientRegistered(String payload) {
        try {
            PatientRegisteredEvent event = objectMapper.readValue(payload, PatientRegisteredEvent.class);
            emailService.sendPatientProfileEmail(event);
        } catch (Exception e) {
            log.error("Failed to process patient-registered event: {}", e.getMessage(), e);
        }
    }
}
