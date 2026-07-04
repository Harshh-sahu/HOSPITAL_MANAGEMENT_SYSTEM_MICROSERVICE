package com.EmailService.EmailServiceMS.service;

import com.EmailService.EmailServiceMS.entity.EmailLog;
import com.EmailService.EmailServiceMS.event.AppointmentCreatedEvent;
import com.EmailService.EmailServiceMS.event.PrescriptionCreatedEvent;
import com.EmailService.EmailServiceMS.event.UserLoginEvent;
import com.EmailService.EmailServiceMS.event.UserRegisteredEvent;
import com.EmailService.EmailServiceMS.repository.EmailLogRepository;
import com.EmailService.EmailServiceMS.template.EmailTemplateBuilder;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;
    private final EmailTemplateBuilder templates;

    @Value("${hms.email.from}")
    private String from;

    public EmailService(JavaMailSender mailSender, EmailLogRepository emailLogRepository, EmailTemplateBuilder templates) {
        this.mailSender = mailSender;
        this.emailLogRepository = emailLogRepository;
        this.templates = templates;
    }

    public void sendWelcomeEmail(UserRegisteredEvent event) {
        String html = templates.welcome(event.getName(), event.getRole());
        send(event.getEmail(), "Welcome to HMS Hospital", html, "WELCOME");
    }

    public void sendLoginAlertEmail(UserLoginEvent event) {
        String when = event.getLoginTime() != null ? event.getLoginTime().format(DATE_TIME) : "just now";
        String html = templates.loginAlert(event.getName(), when);
        send(event.getEmail(), "New Login to Your HMS Account", html, "LOGIN_ALERT");
    }

    public void sendAppointmentEmail(AppointmentCreatedEvent event) {
        String when = event.getAppointmentTime() != null ? event.getAppointmentTime().format(DATE_TIME) : "the scheduled time";
        String html = templates.appointmentPatient(event.getPatientName(), event.getAppointmentId(),
                event.getDoctorName(), when, event.getReason());
        send(event.getPatientEmail(), "Your Appointment is Confirmed", html, "APPOINTMENT");

        sendAppointmentDoctorEmail(event);
    }

    private void sendAppointmentDoctorEmail(AppointmentCreatedEvent event) {
        if (event.getDoctorEmail() == null || event.getDoctorEmail().isBlank()) {
            return;
        }
        String when = event.getAppointmentTime() != null ? event.getAppointmentTime().format(DATE_TIME) : "the scheduled time";
        String html = templates.appointmentDoctor(event.getDoctorName(), event.getPatientName(), when, event.getReason());
        send(event.getDoctorEmail(), "New Appointment Booked", html, "APPOINTMENT_DOCTOR");
    }

    public void sendPrescriptionEmail(PrescriptionCreatedEvent event) {
        String date = event.getPrescriptionDate() != null ? event.getPrescriptionDate().format(DATE) : "today";
        String html = templates.prescription(event.getPatientName(), event.getDoctorName(), date,
                event.getMedicines(), event.getNotes());
        send(event.getPatientEmail(), "Your Prescription is Ready", html, "PRESCRIPTION");
    }

    private void send(String to, String subject, String htmlBody, String type) {
        if (to == null || to.isBlank()) {
            log.warn("Skipping {} email: recipient address is missing", type);
            record(to, subject, type, "SKIPPED", "recipient address missing");
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Sent {} email to {}", type, to);
            record(to, subject, type, "SENT", null);
        } catch (Exception e) {
            log.error("Failed to send {} email to {}: {}", type, to, e.getMessage(), e);
            record(to, subject, type, "FAILED", e.getMessage());
        }
    }

    private void record(String to, String subject, String type, String status, String error) {
        try {
            EmailLog logEntry = new EmailLog();
            logEntry.setRecipient(to);
            logEntry.setSubject(subject);
            logEntry.setEmailType(type);
            logEntry.setStatus(status);
            logEntry.setError(error != null && error.length() > 1000 ? error.substring(0, 1000) : error);
            emailLogRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Failed to persist email log: {}", e.getMessage());
        }
    }
}
