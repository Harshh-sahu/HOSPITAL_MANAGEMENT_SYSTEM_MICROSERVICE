package com.hms.appointment.service;

import com.hms.appointment.client.ProfileClient;
import com.hms.appointment.dto.*;
import com.hms.appointment.entity.Appointment;
import com.hms.appointment.exception.HmsException;
import com.hms.appointment.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private ProfileClient profileClient;

    @Mock
    private ApiService apiService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    void scheduleAppointment_success_setsScheduledAndSaves() {
        AppointmentDTO dto = new AppointmentDTO(null, 1L, 2L, LocalDateTime.now(), null, "Fever", "Notes");
        when(profileClient.doctorExists(2L)).thenReturn(true);
        when(profileClient.patientExists(1L)).thenReturn(true);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(new Appointment(99L, 1L, 2L, dto.getAppointmentTime(), Status.SCHEDULED, "Fever", "Notes"));

        Long id = appointmentService.scheduleAppointment(dto);

        assertEquals(99L, id);
        assertEquals(Status.SCHEDULED, dto.getStatus());

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepository).save(captor.capture());
        assertEquals(Status.SCHEDULED, captor.getValue().getStatus());
    }

    @Test
    void scheduleAppointment_doctorMissing_throws() {
        AppointmentDTO dto = new AppointmentDTO(null, 1L, 2L, LocalDateTime.now(), null, "Fever", "Notes");
        when(profileClient.doctorExists(2L)).thenReturn(false);

        HmsException ex = assertThrows(HmsException.class, () -> appointmentService.scheduleAppointment(dto));
        assertEquals("DOCTOR_NOT_FOUND", ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void scheduleAppointment_patientMissing_throws() {
        AppointmentDTO dto = new AppointmentDTO(null, 1L, 2L, LocalDateTime.now(), null, "Fever", "Notes");
        when(profileClient.doctorExists(2L)).thenReturn(true);
        when(profileClient.patientExists(1L)).thenReturn(false);

        HmsException ex = assertThrows(HmsException.class, () -> appointmentService.scheduleAppointment(dto));
        assertEquals("PATIENT_NOT_FOUND", ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void cancelAppointment_alreadyCancelled_throws() {
        Appointment existing = new Appointment(10L, 1L, 2L, LocalDateTime.now(), Status.CANCELLED, "Fever", "Notes");
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(existing));

        HmsException ex = assertThrows(HmsException.class, () -> appointmentService.cancelAppointment(10L));
        assertEquals("APPOINTMENT_ALREADY_CANCELLED", ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void cancelAppointment_success_updatesStatus() {
        Appointment existing = new Appointment(10L, 1L, 2L, LocalDateTime.now(), Status.SCHEDULED, "Fever", "Notes");
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(existing));

        appointmentService.cancelAppointment(10L);

        assertEquals(Status.CANCELLED, existing.getStatus());
        verify(appointmentRepository).save(existing);
    }

    @Test
    void getAppointmentCountByPatient_returnsRepositoryData() {
        List<MonthlyVisitDTO> expected = List.of(new MonthlyVisitDTO("MARCH", 3L));
        when(appointmentRepository.countCurrentYearVisitsByPatient(1L)).thenReturn(expected);

        List<MonthlyVisitDTO> actual = appointmentService.getAppointmentCountByPatient(1L);

        assertEquals(1, actual.size());
        assertEquals("MARCH", actual.get(0).getMonth());
        assertEquals(3L, actual.get(0).getCount());
    }

    @Test
    void getAppointmentDetailsWithName_missingProfileData_throws() {
        Appointment entity = new Appointment(10L, 1L, 2L, LocalDateTime.now(), Status.SCHEDULED, "Fever", "Notes");
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(entity));
        when(profileClient.getDoctorById(2L)).thenReturn(null);
        when(profileClient.getPatientById(1L)).thenReturn(null);

        HmsException ex = assertThrows(HmsException.class, () -> appointmentService.getAppointmentDetailsWithName(10L));
        assertEquals("DOCTOR_OR_PATIENT_NOT_FOUND", ex.getMessage());
    }

    @Test
    void getAllAppointmentsByPatientId_setsDoctorName() {
        AppointmentDetails details = new AppointmentDetails(1L, 1L, null, null, null, 2L, null, LocalDateTime.now(), Status.SCHEDULED, "Fever", "Notes");
        when(appointmentRepository.findAllByPatientId(1L)).thenReturn(List.of(details));

        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(2L);
        doctorDTO.setName("Dr. Test");
        when(profileClient.getDoctorById(2L)).thenReturn(doctorDTO);

        List<AppointmentDetails> result = appointmentService.getAllAppointmentsByPatientId(1L);

        assertEquals(1, result.size());
        assertEquals("Dr. Test", result.get(0).getDoctorName());
    }

    @Test
    void getAllAppointmentsByDoctorId_setsPatientFields() {
        AppointmentDetails details = new AppointmentDetails(1L, 1L, null, null, null, 2L, null, LocalDateTime.now(), Status.SCHEDULED, "Fever", "Notes");
        when(appointmentRepository.findAllByDoctorId(2L)).thenReturn(List.of(details));

        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(1L);
        patientDTO.setName("Patient Test");
        patientDTO.setEmail("p@test.com");
        patientDTO.setPhone("9999999999");
        when(profileClient.getPatientById(1L)).thenReturn(patientDTO);

        List<AppointmentDetails> result = appointmentService.getAllAppointmentsByDoctorId(2L);

        assertEquals(1, result.size());
        assertEquals("Patient Test", result.get(0).getPatientName());
        assertEquals("p@test.com", result.get(0).getPatientEmail());
        assertEquals("9999999999", result.get(0).getPatientPhone());
    }

    @Test
    void getTodaysAppointments_mapsDoctorAndPatientNames() {
        LocalDateTime now = LocalDateTime.now();
        Appointment ap = new Appointment(1L, 1L, 2L, now, Status.SCHEDULED, "Fever", "Notes");
        when(appointmentRepository.findByAppointmentTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ap));

        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(2L);
        doctorDTO.setName("Dr. Daily");
        when(profileClient.getDoctorById(2L)).thenReturn(doctorDTO);

        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(1L);
        patientDTO.setName("Patient Daily");
        patientDTO.setEmail("daily@test.com");
        patientDTO.setPhone("8888888888");
        when(profileClient.getPatientById(1L)).thenReturn(patientDTO);

        List<AppointmentDetails> result = appointmentService.getTodaysAppointments();

        assertEquals(1, result.size());
        assertEquals("Dr. Daily", result.get(0).getDoctorName());
        assertEquals("Patient Daily", result.get(0).getPatientName());
    }
}
