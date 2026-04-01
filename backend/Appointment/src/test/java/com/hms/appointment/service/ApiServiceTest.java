package com.hms.appointment.service;

import com.hms.appointment.dto.DoctorDTO;
import com.hms.appointment.dto.PatientDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ApiServiceTest {

    private ApiService apiService;
    private WebClient.Builder builder;

    @BeforeEach
    void setUp() {
        apiService = new ApiService();
        builder = Mockito.mock(WebClient.Builder.class, Mockito.RETURNS_DEEP_STUBS);
        ReflectionTestUtils.setField(apiService, "webclient", builder);
    }

    @Test
    void doctorExist_returnsMonoValue() {
        when(builder.build().get().uri(anyString()).retrieve().bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

        Boolean result = apiService.doctorExist(1L).block();

        assertEquals(true, result);
    }

    @Test
    void patientExist_returnsMonoValue() {
        when(builder.build().get().uri(anyString()).retrieve().bodyToMono(Boolean.class)).thenReturn(Mono.just(false));

        Boolean result = apiService.patientExist(1L).block();

        assertEquals(false, result);
    }

    @Test
    void getPatientById_returnsDto() {
        PatientDTO dto = new PatientDTO();
        dto.setId(10L);
        dto.setName("Patient X");
        when(builder.build().get().uri(anyString()).retrieve().bodyToMono(PatientDTO.class)).thenReturn(Mono.just(dto));

        PatientDTO result = apiService.getPatientById(10L).block();

        assertEquals(10L, result.getId());
        assertEquals("Patient X", result.getName());
    }

    @Test
    void getDoctorById_returnsDto() {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(20L);
        dto.setName("Dr. Y");
        when(builder.build().get().uri(anyString()).retrieve().bodyToMono(DoctorDTO.class)).thenReturn(Mono.just(dto));

        DoctorDTO result = apiService.getDoctorById(20L).block();

        assertEquals(20L, result.getId());
        assertEquals("Dr. Y", result.getName());
    }
}

