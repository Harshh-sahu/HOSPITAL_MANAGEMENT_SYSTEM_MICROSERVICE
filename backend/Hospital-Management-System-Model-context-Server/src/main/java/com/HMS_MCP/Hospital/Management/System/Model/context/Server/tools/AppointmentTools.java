package com.HMS_MCP.Hospital.Management.System.Model.context.Server.tools;

import com.HMS_MCP.Hospital.Management.System.Model.context.Server.config.HmsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppointmentTools {

    private final RestClient restClient;
    private final HmsProperties hmsProperties;

    @Tool(description = "Schedule a new appointment for a patient with a doctor. " +
            "appointmentTime format: YYYY-MM-DDTHH:MM:SS (e.g. 2025-07-15T10:30:00). " +
            "reason is the visit reason (e.g. Chest pain, Routine checkup). " +
            "Returns the generated appointment ID.")
    public String scheduleAppointment(Long patientId, Long doctorId, String appointmentTime,
                                      String reason, String notes) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("patientId", patientId);
            body.put("doctorId", doctorId);
            body.put("appointmentTime", appointmentTime);
            body.put("reason", reason);
            body.put("notes", notes);
            body.put("status", "SCHEDULED");

            return restClient.post()
                    .uri(hmsProperties.getAppointmentServiceUrl() + "/appointment/schedule")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error scheduling appointment: " + e.getMessage();
        }
    }

    @Tool(description = "Cancel an existing appointment by its appointment ID. Returns Appointment Cancelled on success.")
    public String cancelAppointment(Long appointmentId) {
        try {
            return restClient.put()
                    .uri(hmsProperties.getAppointmentServiceUrl() + "/appointment/cancel/" + appointmentId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error cancelling appointment: " + e.getMessage();
        }
    }

    @Tool(description = "Get full appointment details including patient name and doctor name by appointment ID. " +
            "Use this for enriched readable details with names instead of raw IDs.")
    public String getAppointmentDetails(Long appointmentId) {
        try {
            return restClient.get()
                    .uri(hmsProperties.getAppointmentServiceUrl() + "/appointment/get/details/" + appointmentId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching appointment details: " + e.getMessage();
        }
    }

    @Tool(description = "Get all appointments (past and upcoming) for a specific patient by patient ID.")
    public String getAppointmentsByPatient(Long patientId) {
        try {
            return restClient.get()
                    .uri(hmsProperties.getAppointmentServiceUrl() + "/appointment/getAllByPatient/" + patientId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching patient appointments: " + e.getMessage();
        }
    }

    @Tool(description = "Get all appointments assigned to a specific doctor by doctor ID.")
    public String getAppointmentsByDoctor(Long doctorId) {
        try {
            return restClient.get()
                    .uri(hmsProperties.getAppointmentServiceUrl() + "/appointment/getAllByDoctor/" + doctorId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching doctor appointments: " + e.getMessage();
        }
    }

    @Tool(description = "Get all appointments scheduled for today with patient and doctor names. Use this to view the hospital daily schedule.")
    public String getTodaysAppointments() {
        try {
            return restClient.get()
                    .uri(hmsProperties.getAppointmentServiceUrl() + "/appointment/today")
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching todays appointments: " + e.getMessage();
        }
    }

    @Tool(description = "Get overall monthly appointment visit counts for the hospital. Returns month-wise statistics for analytics and dashboard reports.")
    public String getMonthlyVisitCounts() {
        try {
            return restClient.get()
                    .uri(hmsProperties.getAppointmentServiceUrl() + "/appointment/visitCount")
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching visit counts: " + e.getMessage();
        }
    }

    @Tool(description = "Create a post-consultation appointment report and medical record. " +
            "diagnosis is the doctor diagnosis text. notes are additional clinical notes. " +
            "Returns the generated record ID.")
    public String createAppointmentReport(Long appointmentId, String diagnosis, String notes) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("appointmentId", appointmentId);
            body.put("diagnosis", diagnosis);
            body.put("notes", notes);

            return restClient.post()
                    .uri(hmsProperties.getAppointmentServiceUrl() + "/appointment/report/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error creating appointment report: " + e.getMessage();
        }
    }

    @Tool(description = "Get the appointment report for a specific appointment by appointment ID. " +
            "Returns the medical record including diagnosis and notes.")
    public String getReportByAppointmentId(Long appointmentId) {
        try {
            return restClient.get()
                    .uri(hmsProperties.getAppointmentServiceUrl() + "/appointment/report/getByAppointmentId/" + appointmentId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching report: " + e.getMessage();
        }
    }

    @Tool(description = "Get a patient complete medical history including all appointment records by patient ID. " +
            "Returns all past records with diagnosis, notes, and appointment details.")
    public String getPatientMedicalHistory(Long patientId) {
        try {
            return restClient.get()
                    .uri(hmsProperties.getAppointmentServiceUrl() + "/appointment/report/getRecordsByPatientId/" + patientId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching medical history: " + e.getMessage();
        }
    }

    @Tool(description = "Get all prescriptions issued to a patient by patient ID. Returns prescription details including medicines prescribed across all visits.")
    public String getPatientPrescriptions(Long patientId) {
        try {
            return restClient.get()
                    .uri(hmsProperties.getAppointmentServiceUrl() + "/appointment/report/getPrescriptionsByPatientId/" + patientId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching prescriptions: " + e.getMessage();
        }
    }

    @Tool(description = "Get reason-wise appointment counts across all appointments in the hospital. Shows the most common visit reasons.")
    public String getAppointmentReasonCounts() {
        try {
            return restClient.get()
                    .uri(hmsProperties.getAppointmentServiceUrl() + "/appointment/countReasons")
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching reason counts: " + e.getMessage();
        }
    }

    @Tool(description = "Get all medicines that have been prescribed to a patient across all their appointments by patient ID.")
    public String getMedicinesByPatient(Long patientId) {
        try {
            return restClient.get()
                    .uri(hmsProperties.getAppointmentServiceUrl() + "/appointment/getMedicinesByPatient/" + patientId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching patient medicines: " + e.getMessage();
        }
    }
}
