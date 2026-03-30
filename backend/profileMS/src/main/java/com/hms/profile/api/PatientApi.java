package com.hms.profile.api;

import com.hms.profile.dto.DoctorDropdown;
import com.hms.profile.dto.PatientDTO;
import com.hms.profile.exception.HmsException;
import com.hms.profile.service.PatientService;
import com.hms.profile.utility.ErrorInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("profile/patient")
@Validated
@Tag(name = "Profile Patient APIs", description = "Patient profile operations")
@SecurityRequirement(name = "X-Secret-Key")
@RequiredArgsConstructor
public class PatientApi {

    private final PatientService patientService;

    @Operation(operationId = "addPatient", summary = "Add patient", description = "Creates patient profile and returns id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created",
                    content = @Content(schema = @Schema(type = "integer", format = "int64", example = "2001"))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping("/add")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Patient payload",
            content = @Content(schema = @Schema(implementation = PatientDTO.class)))
    public ResponseEntity<Long> addPatient(@Valid @RequestBody PatientDTO patientDTO) throws HmsException {
        return new ResponseEntity<>(patientService.addPatient(patientDTO), HttpStatus.CREATED);
    }

    @Operation(operationId = "getPatientById", summary = "Get patient by id", description = "Returns patient profile details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient fetched",
                    content = @Content(schema = @Schema(implementation = PatientDTO.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<PatientDTO> getPatientById(
            @Parameter(description = "Patient id", example = "2001") @PathVariable Long id
    ) throws HmsException {
        return new ResponseEntity<>(patientService.getPatientById(id), HttpStatus.OK);

    }

    @Operation(operationId = "getPatientProfileId", summary = "Get patient profile picture id", description = "Returns media id used as patient profile picture")
    @ApiResponse(responseCode = "200", description = "Profile picture id fetched",
            content = @Content(schema = @Schema(type = "integer", format = "int64", example = "601")))
    @GetMapping("/getProfileId/{id}")
    public ResponseEntity<Long> getProfileId(
            @Parameter(description = "Patient id", example = "2001") @PathVariable Long id
    ) throws HmsException {
        return new ResponseEntity<>(patientService.getPatientById(id).getProfilePictureId(), HttpStatus.OK);   }


    @Operation(operationId = "updatePatient", summary = "Update patient", description = "Updates an existing patient profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient updated",
                    content = @Content(schema = @Schema(implementation = PatientDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PutMapping("/update")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Patient payload",
            content = @Content(schema = @Schema(implementation = PatientDTO.class)))
    public ResponseEntity<PatientDTO> updatePatient(@Valid @RequestBody PatientDTO patientDTO) throws HmsException {

        return new ResponseEntity<>(patientService.updatePatient(patientDTO), HttpStatus.OK);
    }

    @Operation(operationId = "patientExists", summary = "Check patient exists", description = "Returns whether a patient exists by id")
    @ApiResponse(responseCode = "200", description = "Existence response",
            content = @Content(schema = @Schema(type = "boolean", example = "true")))
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> patientExists(
            @Parameter(description = "Patient id", example = "2001") @PathVariable Long id
    ) throws HmsException {
        return new ResponseEntity<>(patientService.patientExists(id), HttpStatus.OK);
    }


    @Operation(operationId = "getPatientsById", summary = "Get patients by ids", description = "Returns lightweight patient list for given ids")
    @ApiResponse(responseCode = "200", description = "Patients fetched",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = DoctorDropdown.class))))
    @GetMapping("/getPatientsById")
    public ResponseEntity<List<DoctorDropdown>> getDoctorsById(
            @Parameter(description = "Patient ids", example = "2001,2002") @RequestParam List<Long> ids
    ) throws HmsException{

        return new ResponseEntity<>(patientService.getPatientsById(ids),HttpStatus.OK);
    }


    @Operation(operationId = "getAllPatients", summary = "Get all patients", description = "Returns all patient profiles")
    @ApiResponse(responseCode = "200", description = "Patients fetched",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PatientDTO.class))))
    @GetMapping("/getAll")
    public ResponseEntity<List<PatientDTO>> getAllPatients() throws HmsException{
        return new ResponseEntity<>(patientService.getAllPatients(),HttpStatus.OK);     }

}

