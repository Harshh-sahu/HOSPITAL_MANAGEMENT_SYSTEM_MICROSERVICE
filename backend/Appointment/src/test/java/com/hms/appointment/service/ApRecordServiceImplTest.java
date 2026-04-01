package com.hms.appointment.service;

import com.hms.appointment.client.ProfileClient;
import com.hms.appointment.dto.ApRecordDTO;
import com.hms.appointment.dto.DoctorName;
import com.hms.appointment.dto.PrescriptionDTO;
import com.hms.appointment.entity.ApRecord;
import com.hms.appointment.entity.Appointment;
import com.hms.appointment.exception.HmsException;
import com.hms.appointment.repository.ApRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApRecordServiceImplTest {

    @Mock
    private PrescriptionService prescriptionService;

    @Mock
    private ApRecordRepository apRecordRepository;

    @Mock
    private ProfileClient profileClient;

    @InjectMocks
    private ApRecordServiceImpl apRecordService;

    @Test
    void createApRecord_existingRecord_throws() {
        ApRecordDTO request = new ApRecordDTO(null, 1L, 2L, 10L, List.of("fever"), "viral", List.of("cbc"), "notes", "none", null, LocalDate.now().plusDays(5), null);
        when(apRecordRepository.findByAppointment_Id(10L)).thenReturn(Optional.of(new ApRecord()));

        HmsException ex = assertThrows(HmsException.class, () -> apRecordService.createApRecord(request));
        assertTrue(ex.getMessage().contains("already exists"));
        verify(apRecordRepository, never()).save(any());
    }

    @Test
    void createApRecord_withPrescription_savesBoth() {
        PrescriptionDTO prescription = new PrescriptionDTO();
        ApRecordDTO request = new ApRecordDTO(null, 1L, 2L, 10L, List.of("fever"), "viral", List.of("cbc"), "notes", "none", prescription, LocalDate.now().plusDays(5), null);

        when(apRecordRepository.findByAppointment_Id(10L)).thenReturn(Optional.empty());
        when(apRecordRepository.save(any(ApRecord.class))).thenReturn(new ApRecord(55L, 1L, 2L, new Appointment(10L), "fever", "viral", "cbc", "notes", "none", LocalDate.now().plusDays(5), LocalDateTime.now()));

        Long id = apRecordService.createApRecord(request);

        assertEquals(55L, id);
        assertNotNull(request.getCreatedAt());
        assertEquals(10L, prescription.getAppointmentId());
        verify(prescriptionService).savePrescription(prescription);
    }

    @Test
    void getApRecordById_notFound_throws() {
        when(apRecordRepository.findById(99L)).thenReturn(Optional.empty());

        HmsException ex = assertThrows(HmsException.class, () -> apRecordService.getApRecordById(99L));
        assertEquals("APPOINTMENT_RECORD_NOT_FOUND", ex.getMessage());
    }

    @Test
    void getApRecordDetailsByAppointmentId_attachesPrescription() {
        ApRecord entity = new ApRecord(77L, 1L, 2L, new Appointment(10L), "fever", "viral", "cbc", "notes", "none", LocalDate.now().plusDays(3), LocalDateTime.now());
        when(apRecordRepository.findByAppointment_Id(10L)).thenReturn(Optional.of(entity));
        when(prescriptionService.getPrescriptionByAppointmentId(10L)).thenReturn(new PrescriptionDTO());

        ApRecordDTO dto = apRecordService.getApRecordDetailsByAppointmentId(10L);

        assertEquals(77L, dto.getId());
        assertNotNull(dto.getPrescription());
    }

    @Test
    void getRecordByPatientId_mapsDoctorName() {
        ApRecord entity = new ApRecord(77L, 1L, 2L, new Appointment(10L), "fever", "viral", "cbc", "notes", "none", LocalDate.now().plusDays(3), LocalDateTime.now());
        when(apRecordRepository.findByPatientId(1L)).thenReturn(List.of(entity));
        when(profileClient.getDoctorsById(List.of(2L))).thenReturn(List.of(new DoctorName(2L, "Dr. B")));

        var result = apRecordService.getRecordByPatientId(1L);

        assertEquals(1, result.size());
        assertEquals("Dr. B", result.get(0).getDoctorName());
    }
}

