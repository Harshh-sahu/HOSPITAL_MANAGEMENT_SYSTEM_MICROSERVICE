package com.hms.profile.api;

import com.hms.profile.dto.PatientDTO;
import com.hms.profile.exception.HmsException;
import com.hms.profile.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("profile/patient")
@Validated
public class PatientApi {


    @Autowired
    private PatientService patientService;

    @PostMapping("/add")
    public ResponseEntity<Long> addPatient(@RequestBody PatientDTO patientDTO) throws HmsException {
        return new ResponseEntity<>(patientService.addPatient(patientDTO), HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) throws HmsException {
        return new ResponseEntity<>(patientService.getPatientById(id), HttpStatus.OK);

    }

@PutMapping("/update")
    public ResponseEntity<PatientDTO> updatePatient(@RequestBody PatientDTO patientDTO) throws HmsException {
        // Assuming there's an update method in the service

        return new ResponseEntity<>(patientService.updatePatient(patientDTO), HttpStatus.OK);
    }
}
