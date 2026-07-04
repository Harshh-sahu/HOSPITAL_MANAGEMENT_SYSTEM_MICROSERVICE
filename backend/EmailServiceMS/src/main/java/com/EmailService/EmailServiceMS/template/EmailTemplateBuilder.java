package com.EmailService.EmailServiceMS.template;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.List;

/**
 * Loads HTML email templates from src/main/resources/templates/email/
 * and substitutes {{PLACEHOLDER}} tokens with runtime values.
 *
 * Templates are self-contained HTML files — open any in a browser to preview.
 * JavaScript is absent by design: all major email clients block it.
 */
@Component
public class EmailTemplateBuilder {

    // ═══════════════════════════════════════════════════════════════════════
    // Public email builders
    // ═══════════════════════════════════════════════════════════════════════

    public String welcome(String name, String role) {
        boolean isDoctor  = "DOCTOR".equalsIgnoreCase(role);
        boolean isPatient = "PATIENT".equalsIgnoreCase(role);

        String roleColor = isDoctor ? "#2563eb" : isPatient ? "#059669" : "#0d9488";
        String roleBg    = isDoctor ? "#eff6ff"  : isPatient ? "#f0fdf4"  : "#f0fdfa";
        String roleIcon  = isDoctor ? "🩺"        : isPatient ? "🧑‍⚕️"      : "👤";
        String roleLabel = capitalize(role != null ? role : "User");

        return load("welcome.html")
            .replace("{{NAME}}",       esc(safe(name)))
            .replace("{{ROLE_COLOR}}", roleColor)
            .replace("{{ROLE_BG}}",    roleBg)
            .replace("{{ROLE_ICON}}",  roleIcon)
            .replace("{{ROLE_LABEL}}", roleLabel)
            .replace("{{YEAR}}",       year());
    }

    public String loginAlert(String name, String when) {
        return load("login-alert.html")
            .replace("{{NAME}}",       esc(safe(name)))
            .replace("{{LOGIN_TIME}}", esc(safe(when)))
            .replace("{{YEAR}}",       year());
    }

    public String appointmentPatient(String name, Long appointmentId,
                                     String doctorName, String when, String reason) {
        String idBadge = "";
        if (appointmentId != null) {
            idBadge = "<div style=\"text-align:center;margin:0 0 24px;\">"
                + "<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:0 auto;\">"
                + "<tr><td style=\"padding:8px 24px;"
                + "background:linear-gradient(135deg,#0d9488,#14b8a6);border-radius:999px;\">"
                + "<span style=\"font-size:12px;font-weight:800;color:#ffffff;letter-spacing:1.5px;"
                + "text-transform:uppercase;\">Appointment &nbsp;#" + appointmentId + "</span>"
                + "</td></tr></table></div>";
        }

        return load("appointment-patient.html")
            .replace("{{NAME}}",                esc(safe(name)))
            .replace("{{APPOINTMENT_ID_BADGE}}", idBadge)
            .replace("{{DOCTOR_NAME}}",          esc(safe(doctorName)))
            .replace("{{APPOINTMENT_TIME}}",     esc(safe(when)))
            .replace("{{REASON}}",               esc(reason != null && !reason.isBlank() ? reason : "General Consultation"))
            .replace("{{YEAR}}",                 year());
    }

    public String appointmentDoctor(String doctorName, String patientName, String when, String reason) {
        return load("appointment-doctor.html")
            .replace("{{DOCTOR_NAME}}",      esc(safe(doctorName)))
            .replace("{{PATIENT_NAME}}",     esc(safe(patientName)))
            .replace("{{APPOINTMENT_TIME}}", esc(safe(when)))
            .replace("{{REASON}}",           esc(reason != null && !reason.isBlank() ? reason : "General Consultation"))
            .replace("{{YEAR}}",             year());
    }

    public String onboardingLetter(String doctorName, String specialization, String department, String date) {
        return load("onboarding-letter.html")
            .replace("{{DOCTOR_NAME}}",    esc(safe(doctorName)))
            .replace("{{SPECIALIZATION}}", esc(safe(specialization)))
            .replace("{{DEPARTMENT}}",     esc(safe(department)))
            .replace("{{ONBOARDED_DATE}}", esc(safe(date)))
            .replace("{{YEAR}}",           year());
    }

    public String appointmentReminder(String patientName, Long appointmentId, String doctorName,
                                      String appointmentTime, String reason) {
        String idBadge = "";
        if (appointmentId != null) {
            idBadge = "<div style=\"text-align:center;margin:0 0 24px;\">"
                + "<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:0 auto;\">"
                + "<tr><td style=\"padding:8px 24px;"
                + "background:linear-gradient(135deg,#d97706,#f59e0b);border-radius:999px;\">"
                + "<span style=\"font-size:12px;font-weight:800;color:#ffffff;letter-spacing:1.5px;"
                + "text-transform:uppercase;\">Appointment &nbsp;#" + appointmentId + "</span>"
                + "</td></tr></table></div>";
        }
        return load("appointment-reminder.html")
            .replace("{{PATIENT_NAME}}",         esc(safe(patientName)))
            .replace("{{APPOINTMENT_ID_BADGE}}", idBadge)
            .replace("{{DOCTOR_NAME}}",          esc(safe(doctorName)))
            .replace("{{APPOINTMENT_TIME}}",     esc(safe(appointmentTime)))
            .replace("{{REASON}}",               esc(reason != null && !reason.isBlank() ? reason : "General Consultation"))
            .replace("{{YEAR}}",                 year());
    }

    public String followUpReminder(String patientName, String doctorName, String followUpDate, String diagnosis) {
        String diagnosisSection = "";
        if (diagnosis != null && !diagnosis.isBlank()) {
            diagnosisSection = "<tr><td style=\"padding:12px 0 0;\">"
                + "<span style=\"font-size:11px;font-weight:700;color:#5b21b6;text-transform:uppercase;letter-spacing:.6px;\">Previous Diagnosis</span><br/>"
                + "<span style=\"font-size:14px;color:#1e293b;\">" + esc(diagnosis) + "</span>"
                + "</td></tr>";
        }
        return load("followup-reminder.html")
            .replace("{{PATIENT_NAME}}",     esc(safe(patientName)))
            .replace("{{DOCTOR_NAME}}",      esc(safe(doctorName)))
            .replace("{{FOLLOWUP_DATE}}",    esc(safe(followUpDate)))
            .replace("{{DIAGNOSIS_SECTION}}", diagnosisSection)
            .replace("{{YEAR}}",             year());
    }

    public String appointmentCancelled(String recipientName, Long appointmentId, String partyLabel,
                                       String partyName, String appointmentTime, String reason) {
        String idBadge = "";
        if (appointmentId != null) {
            idBadge = "<div style=\"text-align:center;margin:0 0 24px;\">"
                + "<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:0 auto;\">"
                + "<tr><td style=\"padding:8px 24px;"
                + "background:linear-gradient(135deg,#dc2626,#ef4444);border-radius:999px;\">"
                + "<span style=\"font-size:12px;font-weight:800;color:#ffffff;letter-spacing:1.5px;"
                + "text-transform:uppercase;\">Appointment &nbsp;#" + appointmentId + "</span>"
                + "</td></tr></table></div>";
        }
        return load("appointment-cancelled.html")
            .replace("{{RECIPIENT_NAME}}",       esc(safe(recipientName)))
            .replace("{{APPOINTMENT_ID_BADGE}}", idBadge)
            .replace("{{PARTY_LABEL}}",          esc(safe(partyLabel)))
            .replace("{{PARTY_NAME}}",           esc(safe(partyName)))
            .replace("{{APPOINTMENT_TIME}}",     esc(safe(appointmentTime)))
            .replace("{{REASON}}",               esc(reason != null && !reason.isBlank() ? reason : "General Consultation"))
            .replace("{{YEAR}}",                 year());
    }

    public String lowStockAlert(String medicineName, Integer currentStock, Integer threshold, Long medicineId) {
        return load("low-stock-alert.html")
            .replace("{{MEDICINE_NAME}}",   esc(safe(medicineName)))
            .replace("{{CURRENT_STOCK}}",   currentStock != null ? String.valueOf(currentStock) : "0")
            .replace("{{THRESHOLD}}",       threshold != null ? String.valueOf(threshold) : "10")
            .replace("{{MEDICINE_ID}}",     medicineId != null ? String.valueOf(medicineId) : "")
            .replace("{{YEAR}}",            year());
    }

    public String patientProfile(String name, String bloodGroup, String phone, String registeredAt) {
        String bloodGroupRow = bloodGroup != null && !bloodGroup.isBlank()
            ? "<tr><td style=\"padding:8px 0;border-bottom:1px solid #86efac;\">"
              + "<span style=\"font-size:11px;font-weight:700;color:#166534;text-transform:uppercase;letter-spacing:.6px;\">Blood Group</span><br/>"
              + "<span style=\"font-size:15px;font-weight:700;color:#1e293b;\">" + esc(bloodGroup.replace("_", " ")) + "</span>"
              + "</td></tr>"
            : "";
        String phoneRow = phone != null && !phone.isBlank()
            ? "<tr><td style=\"padding:8px 0;border-bottom:1px solid #86efac;\">"
              + "<span style=\"font-size:11px;font-weight:700;color:#166534;text-transform:uppercase;letter-spacing:.6px;\">Phone</span><br/>"
              + "<span style=\"font-size:15px;color:#1e293b;\">" + esc(phone) + "</span>"
              + "</td></tr>"
            : "";
        String registeredRow = registeredAt != null && !registeredAt.isBlank()
            ? "<tr><td style=\"padding:8px 0;\">"
              + "<span style=\"font-size:11px;font-weight:700;color:#166534;text-transform:uppercase;letter-spacing:.6px;\">Registered On</span><br/>"
              + "<span style=\"font-size:15px;color:#1e293b;\">" + esc(registeredAt) + "</span>"
              + "</td></tr>"
            : "";
        return load("patient-profile.html")
            .replace("{{NAME}}",            esc(safe(name)))
            .replace("{{BLOOD_GROUP_ROW}}", bloodGroupRow)
            .replace("{{PHONE_ROW}}",       phoneRow)
            .replace("{{REGISTERED_ROW}}", registeredRow)
            .replace("{{YEAR}}",            year());
    }

    public String invoice(String name, Long invoiceId, String saleDate, String totalAmount) {
        return load("invoice.html")
            .replace("{{NAME}}",         esc(safe(name)))
            .replace("{{INVOICE_ID}}",   invoiceId != null ? String.valueOf(invoiceId) : "")
            .replace("{{SALE_DATE}}",    esc(safe(saleDate)))
            .replace("{{TOTAL_AMOUNT}}", esc(safe(totalAmount)))
            .replace("{{YEAR}}",         year());
    }

    public String medicalReport(String name, String doctorName, String date, Long recordId) {
        return load("medical-report.html")
            .replace("{{NAME}}",        esc(safe(name)))
            .replace("{{DOCTOR_NAME}}", esc(safe(doctorName)))
            .replace("{{REPORT_DATE}}", esc(safe(date)))
            .replace("{{RECORD_ID}}",   recordId != null ? String.valueOf(recordId) : "")
            .replace("{{YEAR}}",        year());
    }

    public String prescription(String name, String doctorName, String date,
                               List<String> medicines, String notes) {
        String medicinesRows  = buildMedicineRows(medicines);
        String medicinesCount = String.valueOf(medicines != null ? medicines.size() : 0);
        String notesSection   = buildNotesSection(notes);

        return load("prescription.html")
            .replace("{{NAME}}",              esc(safe(name)))
            .replace("{{DOCTOR_NAME}}",       esc(safe(doctorName)))
            .replace("{{PRESCRIPTION_DATE}}", esc(safe(date)))
            .replace("{{MEDICINES_COUNT}}",   medicinesCount)
            .replace("{{MEDICINES_ROWS}}",    medicinesRows)
            .replace("{{NOTES_SECTION}}",     notesSection)
            .replace("{{YEAR}}",              year());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Template helpers
    // ═══════════════════════════════════════════════════════════════════════

    private String buildMedicineRows(List<String> medicines) {
        if (medicines == null || medicines.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (String medicine : medicines) {
            String rowBg    = (i % 2 == 0) ? "#f8fafc" : "#ffffff";
            String dotColor = i <= 3 ? "#dc2626" : i <= 6 ? "#d97706" : "#059669";
            sb.append(
                "<tr>"
                + "<td style=\"padding:0;background:" + rowBg + ";"
                + "border-bottom:1px solid #f1f5f9;\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>"
                + "<td style=\"padding:14px 16px;width:52px;vertical-align:middle;\">"
                + "<div style=\"width:34px;height:34px;"
                + "background:linear-gradient(135deg," + dotColor + "," + dotColor + "cc);"
                + "border-radius:50%;text-align:center;line-height:34px;"
                + "color:#fff;font-size:13px;font-weight:800;\">" + i + "</div>"
                + "</td>"
                + "<td style=\"padding:14px 16px 14px 0;vertical-align:middle;\">"
                + "<div style=\"font-size:14px;font-weight:600;color:#0f172a;\">" + esc(medicine) + "</div>"
                + "<div style=\"font-size:11px;color:#94a3b8;margin-top:2px;\">As directed by physician</div>"
                + "</td>"
                + "<td style=\"padding:14px 16px;width:28px;vertical-align:middle;text-align:right;\">"
                + "<span style=\"font-size:16px;\">💊</span>"
                + "</td>"
                + "</tr></table>"
                + "</td></tr>"
            );
            i++;
        }
        return sb.toString();
    }

    private String buildNotesSection(String notes) {
        if (notes == null || notes.isBlank()) return "";
        return "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" "
            + "style=\"margin:0 0 20px;\">"
            + "<tr><td style=\"padding:20px;background:#fffbeb;"
            + "border:1.5px solid #d9770633;border-radius:16px;border-left:4px solid #d97706;\">"
            + "<div style=\"font-size:11px;font-weight:700;text-transform:uppercase;letter-spacing:.8px;"
            + "color:#d97706;margin-bottom:8px;\">🩺 Doctor's Notes</div>"
            + "<div style=\"font-size:14px;line-height:1.7;color:#334155;\">" + esc(notes) + "</div>"
            + "</td></tr></table>";
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Utilities
    // ═══════════════════════════════════════════════════════════════════════

    private String load(String filename) {
        String path = "/templates/email/" + filename;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) throw new IllegalStateException("Email template not found: " + path);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load email template: " + filename, e);
        }
    }

    private String year() {
        return String.valueOf(Year.now().getValue());
    }

    private String safe(String value) {
        return (value == null || value.isBlank()) ? "" : value;
    }

    private String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
