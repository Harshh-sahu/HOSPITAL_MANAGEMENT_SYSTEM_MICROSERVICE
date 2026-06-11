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
public class PatientTools {

    private final RestClient restClient;
    private final HmsProperties hmsProperties;

    @Tool(description = "Add a new patient profile to the hospital system. Returns the generated patient ID. " +
            "dob format: YYYY-MM-DD. " +
            "bloodGroup values: A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE, O_POSITIVE, O_NEGATIVE, AB_POSITIVE, AB_NEGATIVE. " +
            "allergies and chronicDiseases are comma-separated strings, use 'None' if not applicable.")
    public String addPatient(String name, String email, String dob, String phone, String address,
                             String aadharNo, String bloodGroup, String allergies, String chronicDiseases) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("name", name);
            body.put("email", email);
            body.put("dob", dob);
            body.put("phone", phone);
            body.put("address", address);
            body.put("aadharNo", aadharNo);
            body.put("bloodGroup", bloodGroup);
            body.put("allergies", allergies);
            body.put("chronicDiseases", chronicDiseases);

            return restClient.post()
                    .uri(hmsProperties.getProfileServiceUrl() + "/profile/patient/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error adding patient: " + e.getMessage();
        }
    }

    @Tool(description = "Get a patient's full profile by patient ID. Returns name, email, blood group, allergies, chronic diseases, contact info, and Aadhaar number.")
    public String getPatientById(Long patientId) {
        try {
            return restClient.get()
                    .uri(hmsProperties.getProfileServiceUrl() + "/profile/patient/get/" + patientId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching patient: " + e.getMessage();
        }
    }

    @Tool(description = "Get all patients registered in the hospital system. Returns a complete list of all patient profiles.")
    public String getAllPatients() {
        try {
            return restClient.get()
                    .uri(hmsProperties.getProfileServiceUrl() + "/profile/patient/getAll")
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching all patients: " + e.getMessage();
        }
    }

    @Tool(description = "Update an existing patient's profile by patient ID. Provide patientId plus all fields to update. Returns the updated patient profile.")
    public String updatePatient(Long patientId, String name, String email, String dob, String phone,
                                String address, String aadharNo, String bloodGroup,
                                String allergies, String chronicDiseases) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("id", patientId);
            body.put("name", name);
            body.put("email", email);
            body.put("dob", dob);
            body.put("phone", phone);
            body.put("address", address);
            body.put("aadharNo", aadharNo);
            body.put("bloodGroup", bloodGroup);
            body.put("allergies", allergies);
            body.put("chronicDiseases", chronicDiseases);

            return restClient.put()
                    .uri(hmsProperties.getProfileServiceUrl() + "/profile/patient/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error updating patient: " + e.getMessage();
        }
    }
}
