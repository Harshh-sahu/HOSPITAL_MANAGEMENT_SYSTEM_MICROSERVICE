package com.hms.profile.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.profile.config.SecurityConfig;
import com.hms.profile.dto.BloodGroup;
import com.hms.profile.dto.DoctorDTO;
import com.hms.profile.dto.PatientDTO;
import com.hms.profile.service.DoctorService;
import com.hms.profile.service.PatientService;
import com.hms.profile.utility.ExceptionControllerAdvice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {DoctorApi.class, PatientApi.class, ExceptionControllerAdvice.class, SecurityConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class ProfileApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DoctorService doctorService;

    @MockitoBean
    private PatientService patientService;

    @Test
    void addDoctor_returnsCreatedId() throws Exception {
        DoctorDTO dto = new DoctorDTO(1L, "Dr Jane", "jane@hms.com", LocalDate.of(1988, 1, 1),
                1L, "99", "Addr", "LIC-1", "Cardio", "Medicine", 12);

        when(doctorService.addDoctor(dto)).thenReturn(1L);

        mockMvc.perform(post("/profile/doctor/add")
                        .header("X-Secret-Key", "SECRET")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    void addPatient_withInvalidBody_returnsBadRequest() throws Exception {
        String invalidJson = "{\"email\":\"bad\"}";

        mockMvc.perform(post("/profile/patient/add")
                        .header("X-Secret-Key", "SECRET")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(400));
    }

    @Test
    void getPatientById_returnsDto() throws Exception {
        PatientDTO dto = new PatientDTO(9L, "Aman", "aman@hms.com", LocalDate.of(1997, 2, 1),
                3L, "999", "Addr", "123456789012", BloodGroup.B_POSITIVE, "None", "None");

        when(patientService.getPatientById(9L)).thenReturn(dto);

        mockMvc.perform(get("/profile/patient/get/9").header("X-Secret-Key", "SECRET"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.name").value("Aman"));
    }

    @Test
    void getDoctorById_whenUnhandledException_returnsInternalServerError() throws Exception {
        when(doctorService.getDoctorById(99L)).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/profile/doctor/get/99").header("X-Secret-Key", "SECRET"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value(500));
    }
}
