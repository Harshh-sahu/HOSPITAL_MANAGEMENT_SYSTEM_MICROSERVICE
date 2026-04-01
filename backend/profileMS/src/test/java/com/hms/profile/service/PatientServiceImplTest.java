package com.hms.profile.service;

import com.hms.profile.dto.BloodGroup;
import com.hms.profile.dto.DoctorDropdown;
import com.hms.profile.dto.PatientDTO;
import com.hms.profile.entity.Patient;
import com.hms.profile.exception.HmsException;
import com.hms.profile.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    @Test
    void addPatient_whenUnique_savesAndReturnsId() {
        PatientDTO dto = new PatientDTO(5L, "Aman", "aman@hms.com", LocalDate.of(1997, 2, 1),
                2L, "9988", "Addr", "123456789012", BloodGroup.A_POSITIVE, "Pollen", "NA");

        when(patientRepository.findByEmail("aman@hms.com")).thenReturn(Optional.empty());
        when(patientRepository.findByAadharNo("123456789012")).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenReturn(dto.toEntity());

        Long id = patientService.addPatient(dto);

        assertEquals(5L, id);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void addPatient_whenAadharExists_throwsHmsException() {
        PatientDTO dto = new PatientDTO(5L, "Aman", "aman@hms.com", LocalDate.now(),
                2L, "9988", "Addr", "123456789012", BloodGroup.A_POSITIVE, "Pollen", "NA");

        when(patientRepository.findByEmail("aman@hms.com")).thenReturn(Optional.empty());
        when(patientRepository.findByAadharNo("123456789012")).thenReturn(Optional.of(dto.toEntity()));

        HmsException ex = assertThrows(HmsException.class, () -> patientService.addPatient(dto));
        assertEquals("PATIENT_ALREADY_EXISTS", ex.getMessage());
    }

    @Test
    void getPatientsById_returnsRepositoryProjection() {
        DoctorDropdown p1 = new DoctorDropdown() {
            @Override
            public Long getId() { return 20L; }

            @Override
            public String getName() { return "Patient A"; }
        };

        when(patientRepository.findAllPatientsDropdownsByIds(List.of(20L))).thenReturn(List.of(p1));

        List<DoctorDropdown> result = patientService.getPatientsById(List.of(20L));

        assertEquals(1, result.size());
        assertEquals("Patient A", result.get(0).getName());
    }
}

