package com.hms.appointment.service;

import com.hms.appointment.client.ProfileClient;
import com.hms.appointment.dto.DoctorName;
import com.hms.appointment.dto.MedicineDTO;
import com.hms.appointment.dto.PrescriptionDTO;
import com.hms.appointment.dto.PrescriptionDetails;
import com.hms.appointment.entity.Appointment;
import com.hms.appointment.entity.Prescription;
import com.hms.appointment.exception.HmsException;
import com.hms.appointment.repository.PrescriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceImplTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private MedicineService medicineService;

    @Mock
    private ProfileClient profileClient;

    @InjectMocks
    private PrescriptionServiceImpl prescriptionService;

    @Test
    void savePrescription_setsDateAndSavesMedicines() {
        PrescriptionDTO request = new PrescriptionDTO(
                null,
                1L,
                2L,
                10L,
                null,
                "Take after food",
                List.of(new MedicineDTO(null, "Paracetamol", 101L, "500mg", "TID", 5, "Oral", "Tablet", "After food", null))
        );

        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(new Prescription(77L, 1L, 2L, new Appointment(10L), LocalDate.now(), "Take after food"));

        Long id = prescriptionService.savePrescription(request);

        assertEquals(77L, id);
        assertNotNull(request.getPrescriptionDate());
        assertEquals(77L, request.getMedicines().get(0).getPrescriptionId());
        verify(medicineService).saveAllMedicines(request.getMedicines());
    }

    @Test
    void getPrescriptionByAppointmentId_notFound_throws() {
        when(prescriptionRepository.findByAppointment_Id(10L)).thenReturn(Optional.empty());

        HmsException ex = assertThrows(HmsException.class, () -> prescriptionService.getPrescriptionByAppointmentId(10L));
        assertEquals("PRESCRIPTION_NOT_FOUND", ex.getMessage());
    }

    @Test
    void getPrescriptionByAppointmentId_success_attachesMedicines() {
        when(prescriptionRepository.findByAppointment_Id(10L))
                .thenReturn(Optional.of(new Prescription(7L, 1L, 2L, new Appointment(10L), LocalDate.now(), "N")));
        when(medicineService.getAllMedicinesByPrescriptionId(7L))
                .thenReturn(List.of(new MedicineDTO(1L, "Paracetamol", 101L, "500mg", "TID", 5, "Oral", "Tablet", "After food", 7L)));

        PrescriptionDTO dto = prescriptionService.getPrescriptionByAppointmentId(10L);

        assertEquals(7L, dto.getId());
        assertEquals(1, dto.getMedicines().size());
        assertEquals("Paracetamol", dto.getMedicines().get(0).getName());
    }

    @Test
    void getPrescriptionsByPatientId_empty_throws() {
        when(prescriptionRepository.findAllByPatientId(1L)).thenReturn(List.of());

        HmsException ex = assertThrows(HmsException.class, () -> prescriptionService.getPrescriptionsByPatientId(1L));
        assertEquals("PRESCRIPTIONS_NOT_FOUND", ex.getMessage());
    }

    @Test
    void getPrescriptions_mapsDoctorAndPatientNames() {
        when(prescriptionRepository.findAll()).thenReturn(List.of(
                new Prescription(1L, 11L, 21L, new Appointment(101L), LocalDate.now(), "N")
        ));
        when(profileClient.getDoctorsById(List.of(21L))).thenReturn(List.of(new DoctorName(21L, "Dr. A")));
        when(profileClient.getPatientsById(List.of(11L))).thenReturn(List.of(new DoctorName(11L, "Patient A")));

        List<PrescriptionDetails> details = prescriptionService.getPrescriptions();

        assertEquals(1, details.size());
        assertEquals("Dr. A", details.get(0).getDoctorName());
        assertEquals("Patient A", details.get(0).getPatientName());
    }
}

