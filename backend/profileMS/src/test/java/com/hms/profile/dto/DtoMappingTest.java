package com.hms.profile.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DtoMappingTest {

    @Test
    void doctorDto_toEntity_mapsFields() {
        DoctorDTO dto = new DoctorDTO(
                1L,
                "Dr Jane",
                "dr.jane@example.com",
                LocalDate.of(1990, 1, 5),
                101L,
                "999",
                "City",
                "LIC-001",
                "Cardiology",
                "Medicine",
                7
        );

        var entity = dto.toEntity();

        assertEquals(1L, entity.getId());
        assertEquals("Dr Jane", entity.getName());
        assertEquals("LIC-001", entity.getLicenseNo());
    }

    @Test
    void patientDto_toEntity_mapsFields() {
        PatientDTO dto = new PatientDTO(
                2L,
                "Aman",
                "aman@example.com",
                LocalDate.of(1998, 8, 10),
                201L,
                "888",
                "Town",
                "123456789012",
                BloodGroup.O_POSITIVE,
                "Dust",
                "Diabetes"
        );

        var entity = dto.toEntity();

        assertEquals(2L, entity.getId());
        assertEquals("Aman", entity.getName());
        assertEquals("123456789012", entity.getAadharNo());
    }
}

