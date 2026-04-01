package com.hms.profile.service;

import com.hms.profile.dto.DoctorDTO;
import com.hms.profile.dto.DoctorDropdown;
import com.hms.profile.entity.Doctor;
import com.hms.profile.repository.DoctorRepository;
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
class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    @Test
    void addDoctor_whenUnique_savesAndReturnsId() {
        DoctorDTO dto = new DoctorDTO(1L, "Dr Jane", "jane@hms.com", LocalDate.of(1989, 4, 5), 1L,
                "999", "Addr", "LIC-22", "Cardio", "Medicine", 10);

        when(doctorRepository.findByEmail("jane@hms.com")).thenReturn(Optional.empty());
        when(doctorRepository.findByLicenseNo("LIC-22")).thenReturn(Optional.empty());
        when(doctorRepository.save(any(Doctor.class))).thenReturn(dto.toEntity());

        Long id = doctorService.addDoctor(dto);

        assertEquals(1L, id);
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void addDoctor_whenEmailExists_throwsException() {
        DoctorDTO dto = new DoctorDTO(1L, "Dr Jane", "jane@hms.com", LocalDate.now(), 1L,
                "999", "Addr", "LIC-22", "Cardio", "Medicine", 10);

        when(doctorRepository.findByEmail("jane@hms.com")).thenReturn(Optional.of(dto.toEntity()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> doctorService.addDoctor(dto));
        assertEquals("DOCTOR_ALREADY_EXISTS", ex.getMessage());
    }

    @Test
    void getDoctorsById_returnsRepositoryProjection() {
        DoctorDropdown d1 = new DoctorDropdown() {
            @Override
            public Long getId() { return 10L; }

            @Override
            public String getName() { return "Dr A"; }
        };

        when(doctorRepository.findAllDoctorDropdownsByIds(List.of(10L))).thenReturn(List.of(d1));

        List<DoctorDropdown> result = doctorService.getDoctorsById(List.of(10L));

        assertEquals(1, result.size());
        assertEquals("Dr A", result.get(0).getName());
    }
}

