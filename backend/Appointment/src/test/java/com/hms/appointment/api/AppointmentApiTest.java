package com.hms.appointment.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.appointment.dto.AppointmentDTO;
import com.hms.appointment.dto.MonthlyVisitDTO;
import com.hms.appointment.dto.Status;
import com.hms.appointment.service.AppointmentService;
import com.hms.appointment.service.PrescriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentAPI.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppointmentService appointmentService;

    @MockBean
    private PrescriptionService prescriptionService;

    @Test
    void scheduleAppointment_validPayload_returns201() throws Exception {
        AppointmentDTO dto = new AppointmentDTO(null, 1L, 2L, LocalDateTime.now().plusDays(1), Status.SCHEDULED, "Fever", "Notes");
        when(appointmentService.scheduleAppointment(any(AppointmentDTO.class))).thenReturn(101L);

        mockMvc.perform(post("/appointment/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(101));
    }

    @Test
    void scheduleAppointment_missingDoctorId_returns400() throws Exception {
        String payload = """
                {
                  \"patientId\": 1,
                  \"appointmentTime\": \"2026-04-02T10:30:00\",
                  \"reason\": \"Fever\"
                }
                """;

        mockMvc.perform(post("/appointment/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(org.hamcrest.Matchers.containsString("doctorId is required")));
    }

    @Test
    void scheduleAppointment_missingPatientId_returns400() throws Exception {
        String payload = """
                {
                  \"doctorId\": 2,
                  \"appointmentTime\": \"2026-04-02T10:30:00\"
                }
                """;

        mockMvc.perform(post("/appointment/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(org.hamcrest.Matchers.containsString("patientId is required")));
    }

    @Test
    void cancelAppointment_returns204() throws Exception {
        mockMvc.perform(put("/appointment/cancel/55"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAppointmentDetails_returns200() throws Exception {
        AppointmentDTO dto = new AppointmentDTO(10L, 1L, 2L, LocalDateTime.now(), Status.SCHEDULED, "Fever", "Notes");
        when(appointmentService.getAppointmentDetails(10L)).thenReturn(dto);

        mockMvc.perform(get("/appointment/get/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.doctorId").value(2));
    }

    @Test
    void getAppointmentCountByPatient_returns200WithList() throws Exception {
        when(appointmentService.getAppointmentCountByPatient(1L)).thenReturn(List.of(new MonthlyVisitDTO("MARCH", 4L)));

        mockMvc.perform(get("/appointment/countByPatient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].month").value("MARCH"))
                .andExpect(jsonPath("$[0].count").value(4));
    }
}

