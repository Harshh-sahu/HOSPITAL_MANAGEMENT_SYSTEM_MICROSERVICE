package com.hms.profile.api;

import com.hms.profile.dto.DoctorDTO;
import com.hms.profile.exception.HmsException;
import com.hms.profile.repository.DoctorRepository;
import com.hms.profile.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller

@RequestMapping("profile/doctor")
@Validated
public class DoctorApi {


    @Autowired
    private DoctorService doctorService;

    @PostMapping("/add")
    public ResponseEntity<Long> addDoctor(@RequestBody DoctorDTO DoctorDTO) throws HmsException {
        return new ResponseEntity<>(doctorService.addDoctor(DoctorDTO), HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) throws HmsException {
        return new ResponseEntity<>(doctorService.getDoctorById(id), HttpStatus.OK);

    }

    @PutMapping("/update")
    public ResponseEntity<DoctorDTO> updateDoctor(@RequestBody DoctorDTO doctorDTO) throws HmsException {
        // Assuming there's an update method in the service
        return new ResponseEntity<>(doctorService.updateDoctor(doctorDTO), HttpStatus.OK);
    }


}
