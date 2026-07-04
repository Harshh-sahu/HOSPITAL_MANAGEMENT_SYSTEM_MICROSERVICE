package com.EmailService.EmailServiceMS.kafka;

import com.EmailService.EmailServiceMS.event.AppointmentCreatedEvent;
import com.EmailService.EmailServiceMS.event.PrescriptionCreatedEvent;
import com.EmailService.EmailServiceMS.event.ReportCreatedEvent;
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
}
