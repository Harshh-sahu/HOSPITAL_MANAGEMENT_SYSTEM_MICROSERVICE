package com.hms.appointment.api;

import com.hms.appointment.dto.ApRecordDTO;
import com.hms.appointment.dto.PrescriptionDetails;
import com.hms.appointment.dto.RecordDetails;
import com.hms.appointment.exception.HmsException;
import com.hms.appointment.service.ApRecordService;
import com.hms.appointment.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/appointment/report")
@Validated
@RequiredArgsConstructor
public class ApRecordAPI {
private final PrescriptionService  prescriptionService;

    private final ApRecordService apRecordService;

    @PostMapping("/create")
    public ResponseEntity<Long> createAppointmentReport(@RequestBody ApRecordDTO request) throws HmsException {
        System.out.println(request+"  in api");
        return new ResponseEntity<>( apRecordService.createApRecord(request), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateAppointmentReport(@RequestBody ApRecordDTO request) throws  HmsException{
        apRecordService.updateApRecord(request);
        return new ResponseEntity<>("Appointment Report Updated",HttpStatus.OK);
    }

    @GetMapping("/getByAppointmentId/{appointmentId}")
    public ResponseEntity<ApRecordDTO> getAppointmentReportByAppointmentId(@PathVariable Long appointmentId) throws HmsException {
        return new ResponseEntity<>(apRecordService.getApRecordByAppointmentId(appointmentId),HttpStatus.OK);
    }

@GetMapping("/getById/{recordId}")
    public ResponseEntity<ApRecordDTO> getAppointmentReportById(@PathVariable Long recordId) throws  HmsException{
        return new ResponseEntity<>(apRecordService.getApRecordById(recordId),HttpStatus.OK);
}



    @GetMapping("/getDetailsByAppointmentId/{recordId}")
    public ResponseEntity<ApRecordDTO> getAppointmentReportDeatilsByAppointmentId(@PathVariable Long recordId) throws  HmsException{
        return new ResponseEntity<>(apRecordService.getApRecordDetailsByAppointmentId(recordId),HttpStatus.OK);
    }

    @GetMapping("/getRecordsByPatientId/{patientId}")
    public ResponseEntity<List<RecordDetails>> getRecordsByPatientId(@PathVariable Long patientId) throws HmsException{
        return new ResponseEntity<>(apRecordService.getRecordByPatientId(patientId),HttpStatus.OK);
    }

    @GetMapping("/isRecordExists/{appointmentId}")
    public ResponseEntity<Boolean> isRecordExists(@PathVariable Long appointmentId) throws HmsException{
        return new ResponseEntity<>(apRecordService.isRecordExists(appointmentId),HttpStatus.OK);
    }
    @GetMapping("/getPrescriptionsByPatientId/{patientId}")
    public ResponseEntity<List<PrescriptionDetails>> getPrescriptionsByPatientId(@PathVariable Long patientId) throws HmsException{
        return new ResponseEntity<>(prescriptionService.getPrescriptionsByPatientId(patientId),HttpStatus.OK);
    }

}
