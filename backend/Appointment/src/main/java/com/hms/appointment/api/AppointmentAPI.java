package com.hms.appointment.api;

import com.hms.appointment.dto.*;
import com.hms.appointment.exception.HmsException;
import com.hms.appointment.service.AppointmentService;
import com.hms.appointment.service.PrescriptionService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
@CrossOrigin
@Validated
public class AppointmentAPI {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PrescriptionService prescriptionService;
    @PostMapping("/schedule")
    public ResponseEntity<Long> scheduleAppointment(@RequestBody AppointmentDTO appointmentDTO) {
   return new ResponseEntity<>(appointmentService.scheduleAppointment(appointmentDTO), HttpStatus.CREATED);
    }

    @PutMapping("/cancel/{appointmentId}")
    public  ResponseEntity<String> cancelAppointment(@PathVariable Long appointmentId) throws HmsException {
        appointmentService.cancelAppointment(appointmentId);
        return new ResponseEntity<>("Appointment Cancelled",HttpStatus.NO_CONTENT);
    }

    @GetMapping("/get/{appointmentId}")
    public ResponseEntity<AppointmentDTO> getAppointmentDetails(@PathVariable Long appointmentId) throws HmsException {
        AppointmentDTO appointmentDTO = appointmentService.getAppointmentDetails(appointmentId);
        return new ResponseEntity<>(appointmentDTO, HttpStatus.OK);
    }

    @GetMapping("/get/details/{appointmentId}")
    public ResponseEntity<AppointmentDetails> getAppointmentDetailsWithName(@PathVariable Long appointmentId) throws HmsException {
        AppointmentDetails  appointmentDetails= appointmentService.getAppointmentDetailsWithName(appointmentId);
        return new ResponseEntity<>(appointmentDetails, HttpStatus.OK);
    }
@GetMapping("/getAllByPatient/{patientId}")
    public ResponseEntity<?> getAllAppointmentsByPatientId(@PathVariable Long patientId) throws HmsException {
        return new ResponseEntity<>(appointmentService.getAllAppointmentsByPatientId(patientId), HttpStatus.OK);
    }
@GetMapping("/getAllByDoctor/{doctorId}")
    public ResponseEntity<?> getAllAppointmentsByDoctorId(@PathVariable Long doctorId) throws HmsException {
        return new ResponseEntity<>(appointmentService.getAllAppointmentsByDoctorId(doctorId), HttpStatus.OK);
    }

    @GetMapping("/countByPatient/{patientId}")
    public ResponseEntity<List<MonthlyVisitDTO>> getAppointmentCountByPatient(@PathVariable Long patientId) throws HmsException {
        return new ResponseEntity<>(appointmentService.getAppointmentCountByPatient(patientId), HttpStatus.OK);
    }
    @GetMapping("/countByDoctor/{doctorId}")
    public ResponseEntity<List<MonthlyVisitDTO>> getAppointmentCountByDoctor(@PathVariable Long doctorId) throws HmsException {
        return new ResponseEntity<>(appointmentService.getAppointmentCountByDoctor(doctorId), HttpStatus.OK);
    }
    @GetMapping("/visitCount")
    public ResponseEntity<List<MonthlyVisitDTO>> getAppointmentCounts() throws HmsException {
        return new ResponseEntity<>(appointmentService.getAppointmentCount(), HttpStatus.OK);
    }


    @GetMapping("/countReasonsByPatient/{patientId}")
    public ResponseEntity<List<ReasonCountDTO>> getAppointmentReasonsCountByPatient(@PathVariable Long patientId) throws HmsException {
        return new ResponseEntity<>(appointmentService.getReasonCountByPatient(patientId), HttpStatus.OK);
    }


    @GetMapping("/countReasonsByDoctor/{doctorId}")
    public ResponseEntity<List<ReasonCountDTO>> getAppointmentReasonsCountByDoctor(@PathVariable Long doctorId) throws HmsException {
        return new ResponseEntity<>(appointmentService.getReasonCountByDoctor(doctorId), HttpStatus.OK);
    }

    @GetMapping("/countReasons")
    public ResponseEntity<List<ReasonCountDTO>> getAppointmentReasonsCount() throws HmsException {
        return new ResponseEntity<>(appointmentService.getReasonCount(), HttpStatus.OK);
    }


    @GetMapping("/getMedicinesByPatient/{patientId}")
    public ResponseEntity<List<MedicineDTO>> getMedicinesByPatient(@PathVariable Long patientId) throws HmsException {
        return new ResponseEntity<>(prescriptionService.getMedicineByPatientId(patientId), HttpStatus.OK);
    }

    @GetMapping("/today")
    public ResponseEntity<List<AppointmentDetails>>  getTodaysAppointments() throws HmsException {
        return new ResponseEntity<>(appointmentService.getTodaysAppointments(), HttpStatus.OK);
    }
}
