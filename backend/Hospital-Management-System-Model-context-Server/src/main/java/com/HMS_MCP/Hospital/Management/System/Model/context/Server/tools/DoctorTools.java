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
public class DoctorTools {

    private final RestClient restClient;
    private final HmsProperties hmsProperties;

    @Tool(description = "Add a new doctor profile to the hospital. Returns the generated doctor ID. dob format: YYYY-MM-DD. specialization examples: Cardiology, Neurology, Orthopedics. department examples: ICU, OPD, Emergency.")
    public String addDoctor(String name, String email, String dob, String phone, String address,
                            String licenseNo, String specialization, String department, Integer totalExp) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("name", name);
            body.put("email", email);
            body.put("dob", dob);
            body.put("phone", phone);
            body.put("address", address);
            body.put("licenseNo", licenseNo);
            body.put("specialization", specialization);
            body.put("department", department);
            body.put("totalExp", totalExp);

            return restClient.post()
                    .uri(hmsProperties.getProfileServiceUrl() + "/profile/doctor/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error adding doctor: " + e.getMessage();
        }
    }

    @Tool(description = "Get a doctor's full profile details by doctor ID. Returns name, email, specialization, department, license number, years of experience, phone, and address.")
    public String getDoctorById(Long doctorId) {
        try {
            return restClient.get()
                    .uri(hmsProperties.getProfileServiceUrl() + "/profile/doctor/get/" + doctorId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching doctor: " + e.getMessage();
        }
    }

    @Tool(description = "Get all doctors registered in the hospital system. Returns a list of all doctor profiles with complete details including specialization and department.")
    public String getAllDoctors() {
        try {
            return restClient.get()
                    .uri(hmsProperties.getProfileServiceUrl() + "/profile/doctor/getAll")
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching all doctors: " + e.getMessage();
        }
    }

    @Tool(description = "Get a lightweight dropdown list of all doctors with IDs and names. Useful when scheduling appointments to select a doctor.")
    public String getDoctorDropdowns() {
        try {
            return restClient.get()
                    .uri(hmsProperties.getProfileServiceUrl() + "/profile/doctor/dropdowns")
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching doctor list: " + e.getMessage();
        }
    }

    @Tool(description = "Update an existing doctor's profile by doctor ID. Provide doctorId plus all fields to update. Returns the updated doctor profile.")
    public String updateDoctor(Long doctorId, String name, String email, String dob, String phone,
                               String address, String licenseNo, String specialization,
                               String department, Integer totalExp) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("id", doctorId);
            body.put("name", name);
            body.put("email", email);
            body.put("dob", dob);
            body.put("phone", phone);
            body.put("address", address);
            body.put("licenseNo", licenseNo);
            body.put("specialization", specialization);
            body.put("department", department);
            body.put("totalExp", totalExp);

            return restClient.put()
                    .uri(hmsProperties.getProfileServiceUrl() + "/profile/doctor/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error updating doctor: " + e.getMessage();
        }
    }
}
