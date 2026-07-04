package com.EmailService.EmailServiceMS.service;

import com.EmailService.EmailServiceMS.entity.EmailLog;
import com.EmailService.EmailServiceMS.event.AppointmentCreatedEvent;
import com.EmailService.EmailServiceMS.event.DoctorOnboardedEvent;
import com.EmailService.EmailServiceMS.event.PrescriptionCreatedEvent;
import com.EmailService.EmailServiceMS.event.ReportCreatedEvent;
import com.EmailService.EmailServiceMS.event.SaleCreatedEvent;
import com.EmailService.EmailServiceMS.event.UserLoginEvent;
import com.EmailService.EmailServiceMS.event.UserRegisteredEvent;
import com.EmailService.EmailServiceMS.repository.EmailLogRepository;
import com.EmailService.EmailServiceMS.template.EmailTemplateBuilder;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
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
    private final PdfReportGenerator pdfReportGenerator;
    private final PdfAppointmentGenerator pdfAppointmentGenerator;
    private final PdfPrescriptionGenerator pdfPrescriptionGenerator;
    private final PdfInvoiceGenerator pdfInvoiceGenerator;
    private final PdfOnboardingLetterGenerator pdfOnboardingLetterGenerator;

    @Value("${hms.email.from}")
    private String from;

    public EmailService(JavaMailSender mailSender, EmailLogRepository emailLogRepository,
                        EmailTemplateBuilder templates, PdfReportGenerator pdfReportGenerator,
                        PdfAppointmentGenerator pdfAppointmentGenerator,
                        PdfPrescriptionGenerator pdfPrescriptionGenerator,
                        PdfInvoiceGenerator pdfInvoiceGenerator,
                        PdfOnboardingLetterGenerator pdfOnboardingLetterGenerator) {
        this.mailSender = mailSender;
        this.emailLogRepository = emailLogRepository;
        this.templates = templates;
        this.pdfReportGenerator = pdfReportGenerator;
        this.pdfAppointmentGenerator = pdfAppointmentGenerator;
        this.pdfPrescriptionGenerator = pdfPrescriptionGenerator;
        this.pdfInvoiceGenerator = pdfInvoiceGenerator;
        this.pdfOnboardingLetterGenerator = pdfOnboardingLetterGenerator;
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
        try {
            byte[] pdf = pdfAppointmentGenerator.generate(event);
            String filename = "Appointment_Confirmation_" + event.getAppointmentId() + ".pdf";
            sendWithAttachment(event.getPatientEmail(), "Your Appointment is Confirmed — HMS Hospital",
                    html, "APPOINTMENT", pdf, filename);
        } catch (Exception e) {
            log.error("Failed to generate appointment PDF for appointmentId={}: {}", event.getAppointmentId(), e.getMessage(), e);
            send(event.getPatientEmail(), "Your Appointment is Confirmed", html, "APPOINTMENT");
        }
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
        try {
            byte[] pdf = pdfPrescriptionGenerator.generate(event);
            String filename = "Prescription_" + event.getPrescriptionId() + ".pdf";
            sendWithAttachment(event.getPatientEmail(), "Your Prescription is Ready — HMS Hospital",
                    html, "PRESCRIPTION", pdf, filename);
        } catch (Exception e) {
            log.error("Failed to generate prescription PDF for prescriptionId={}: {}", event.getPrescriptionId(), e.getMessage(), e);
            send(event.getPatientEmail(), "Your Prescription is Ready", html, "PRESCRIPTION");
        }
    }

    public void sendOnboardingEmail(DoctorOnboardedEvent event) {
        if (event.getEmail() == null || event.getEmail().isBlank()) {
            log.warn("Skipping ONBOARDING email: recipient address is missing for doctorId={}", event.getDoctorId());
            return;
        }
        String date = event.getOnboardedAt() != null ? event.getOnboardedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) : "today";
        String html = templates.onboardingLetter(event.getName(), event.getSpecialization(), event.getDepartment(), date);
        try {
            byte[] pdf = pdfOnboardingLetterGenerator.generate(event);
            String safeName = event.getName() != null ? event.getName().replace(" ", "_") : "Doctor";
            String filename = "Onboarding_Letter_Dr_" + safeName + ".pdf";
            sendWithAttachment(event.getEmail(), "Welcome to HMS Hospital — Appointment Letter",
                    html, "ONBOARDING", pdf, filename);
        } catch (Exception e) {
            log.error("Failed to generate onboarding PDF for doctorId={}: {}", event.getDoctorId(), e.getMessage(), e);
            send(event.getEmail(), "Welcome to HMS Hospital", html, "ONBOARDING");
        }
    }

    public void sendInvoiceEmail(SaleCreatedEvent event) {
        if (event.getBuyerEmail() == null || event.getBuyerEmail().isBlank()) {
            log.warn("Skipping INVOICE email: recipient address is missing for saleId={}", event.getSaleId());
            return;
        }
        String date = event.getSaleDate() != null ? event.getSaleDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")) : "today";
        String total = event.getTotalAmount() != null ? "₹" + String.format("%.2f", event.getTotalAmount()) : "—";
        String html = templates.invoice(event.getBuyerName(), event.getSaleId(), date, total);
        try {
            byte[] pdf = pdfInvoiceGenerator.generate(event);
            String filename = "Invoice_" + event.getSaleId() + ".pdf";
            sendWithAttachment(event.getBuyerEmail(), "Your HMS Pharmacy Invoice — #" + event.getSaleId(),
                    html, "INVOICE", pdf, filename);
        } catch (Exception e) {
            log.error("Failed to generate invoice PDF for saleId={}: {}", event.getSaleId(), e.getMessage(), e);
            send(event.getBuyerEmail(), "Your HMS Pharmacy Invoice", html, "INVOICE");
        }
    }

    public void sendMedicalReportEmail(ReportCreatedEvent event) {
        if (event.getPatientEmail() == null || event.getPatientEmail().isBlank()) {
            log.warn("Skipping MEDICAL_REPORT email: recipient address is missing for recordId={}", event.getRecordId());
            return;
        }
        try {
            String date = event.getCreatedAt() != null ? event.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "today";
            String html = templates.medicalReport(event.getPatientName(), event.getDoctorName(), date, event.getRecordId());
            byte[] pdf = pdfReportGenerator.generate(event);
            String filename = "Medical_Report_" + event.getRecordId() + ".pdf";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(event.getPatientEmail());
            helper.setSubject("Your Medical Report — HMS Hospital");
            helper.setText(html, true);
            helper.addAttachment(filename, new ByteArrayDataSource(pdf, "application/pdf"));
            mailSender.send(message);
            log.info("Sent MEDICAL_REPORT email with PDF attachment to {}", event.getPatientEmail());
            record(event.getPatientEmail(), "Your Medical Report — HMS Hospital", "MEDICAL_REPORT", "SENT", null);
        } catch (Exception e) {
            log.error("Failed to send MEDICAL_REPORT email to {}: {}", event.getPatientEmail(), e.getMessage(), e);
            record(event.getPatientEmail(), "Your Medical Report — HMS Hospital", "MEDICAL_REPORT", "FAILED", e.getMessage());
        }
    }

    private void sendWithAttachment(String to, String subject, String htmlBody, String type,
                                    byte[] attachmentBytes, String attachmentFilename) {
        if (to == null || to.isBlank()) {
            log.warn("Skipping {} email: recipient address is missing", type);
            record(to, subject, type, "SKIPPED", "recipient address missing");
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.addAttachment(attachmentFilename, new ByteArrayDataSource(attachmentBytes, "application/pdf"));
            mailSender.send(message);
            log.info("Sent {} email with attachment to {}", type, to);
            record(to, subject, type, "SENT", null);
        } catch (Exception e) {
            log.error("Failed to send {} email to {}: {}", type, to, e.getMessage(), e);
            record(to, subject, type, "FAILED", e.getMessage());
        }
    }

    private void send(String to, String subject, String htmlBody, String type) {
        if (to == null || to.isBlank()) {
            log.warn("Skipping {} email: recipient address is missing", type);
            record(to, subject, type, "SKIPPED", "recipient address missing");
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
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
