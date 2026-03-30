package com.hms.profile.api;

import com.hms.profile.dto.DoctorDTO;
import com.hms.profile.dto.DoctorDropdown;
import com.hms.profile.exception.HmsException;
import com.hms.profile.service.DoctorService;
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

@RequestMapping("profile/doctor")
@Validated
@Tag(name = "Profile Doctor APIs", description = "Doctor profile operations")
@SecurityRequirement(name = "X-Secret-Key")
@RequiredArgsConstructor
public class DoctorApi {

    private final DoctorService doctorService;

    @Operation(operationId = "addDoctor", summary = "Add doctor", description = "Creates doctor profile and returns id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor created",
                    content = @Content(schema = @Schema(type = "integer", format = "int64", example = "1001"))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping("/add")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Doctor payload",
            content = @Content(schema = @Schema(implementation = DoctorDTO.class)))
    public ResponseEntity<Long> addDoctor(@Valid @RequestBody DoctorDTO doctorDTO) throws HmsException {
        return new ResponseEntity<>(doctorService.addDoctor(doctorDTO), HttpStatus.CREATED);
    }

    @Operation(operationId = "getDoctorById", summary = "Get doctor by id", description = "Returns doctor profile details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor fetched",
                    content = @Content(schema = @Schema(implementation = DoctorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(
            @Parameter(description = "Doctor id", example = "1001") @PathVariable Long id
    ) throws HmsException {
        return new ResponseEntity<>(doctorService.getDoctorById(id), HttpStatus.OK);

    }

    @Operation(operationId = "getDoctorProfileId", summary = "Get doctor profile picture id", description = "Returns media id used as doctor profile picture")
    @ApiResponse(responseCode = "200", description = "Profile picture id fetched",
            content = @Content(schema = @Schema(type = "integer", format = "int64", example = "501")))
    @GetMapping("/getProfileId/{id}")
    public ResponseEntity<Long> getProfileId(
            @Parameter(description = "Doctor id", example = "1001") @PathVariable Long id
    ) throws HmsException {
        return new ResponseEntity<>(doctorService.getDoctorById(id).getProfilePictureId(), HttpStatus.OK);   }

    @Operation(operationId = "updateDoctor", summary = "Update doctor", description = "Updates an existing doctor profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor updated",
                    content = @Content(schema = @Schema(implementation = DoctorDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PutMapping("/update")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Doctor payload",
            content = @Content(schema = @Schema(implementation = DoctorDTO.class)))
    public ResponseEntity<DoctorDTO> updateDoctor(@Valid @RequestBody DoctorDTO doctorDTO) throws HmsException {
        return new ResponseEntity<>(doctorService.updateDoctor(doctorDTO), HttpStatus.OK);
    }

    @Operation(operationId = "doctorExists", summary = "Check doctor exists", description = "Returns whether a doctor exists by id")
    @ApiResponse(responseCode = "200", description = "Existence response",
            content = @Content(schema = @Schema(type = "boolean", example = "true")))
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> doctorExists(
            @Parameter(description = "Doctor id", example = "1001") @PathVariable Long id
    ) throws HmsException {
        Boolean exists = doctorService.doctorExists(id);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    @Operation(operationId = "getDoctors", summary = "Get doctor dropdown list", description = "Returns lightweight doctor list for dropdowns")
    @ApiResponse(responseCode = "200", description = "Doctors fetched",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = DoctorDropdown.class))))
    @GetMapping("/dropdowns")
    public ResponseEntity<List<DoctorDropdown>> getDoctors() throws HmsException {
        return new ResponseEntity<>(doctorService.getDoctorDropdowns(), HttpStatus.OK);
    }


    @Operation(operationId = "getDoctorsById", summary = "Get doctors by ids", description = "Returns lightweight doctor list for given ids")
    @ApiResponse(responseCode = "200", description = "Doctors fetched",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = DoctorDropdown.class))))
    @GetMapping("/getDoctorsById")
    public ResponseEntity<List<DoctorDropdown>> getDoctorsById(
            @Parameter(description = "Doctor ids", example = "1001,1002") @RequestParam List<Long> ids
    ) throws HmsException{

        return new ResponseEntity<>(doctorService.getDoctorsById(ids),HttpStatus.OK);
    }

    @Operation(operationId = "getAllDoctors", summary = "Get all doctors", description = "Returns all doctor profiles")
    @ApiResponse(responseCode = "200", description = "Doctors fetched",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = DoctorDTO.class))))
    @GetMapping("/getAll")
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() throws HmsException{
        return new ResponseEntity<>(doctorService.getAllDoctors(),HttpStatus.OK);}
}
