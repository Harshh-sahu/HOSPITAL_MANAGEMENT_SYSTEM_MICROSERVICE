package com.hms.appointment.api;

import com.hms.appointment.dto.*;
import com.hms.appointment.exception.ErrorInfo;
import com.hms.appointment.exception.HmsException;
import com.hms.appointment.service.AppointmentService;
import com.hms.appointment.service.PrescriptionService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
@CrossOrigin
@Validated
@Tag(name = "Appointment APIs", description = "Operations for scheduling, querying and analytics of appointments")
@SecurityRequirement(name = "X-Secret-Key")
public class AppointmentAPI {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Operation(operationId = "scheduleAppointment", summary = "Schedule appointment", description = "Creates a new appointment and returns generated appointment id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Appointment created",
                    content = @Content(schema = @Schema(type = "integer", format = "int64", example = "1001"))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business or processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping("/schedule")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Appointment payload",
            content = @Content(schema = @Schema(implementation = AppointmentDTO.class)))
    public ResponseEntity<Long> scheduleAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO) {
   return new ResponseEntity<>(appointmentService.scheduleAppointment(appointmentDTO), HttpStatus.CREATED);
    }

    @Operation(operationId = "cancelAppointment", summary = "Cancel appointment", description = "Cancels an existing appointment by appointment id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Appointment cancelled"),
            @ApiResponse(responseCode = "500", description = "Business or processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PutMapping("/cancel/{appointmentId}")
    public  ResponseEntity<String> cancelAppointment(
            @Parameter(description = "Appointment id", example = "1001") @PathVariable Long appointmentId
    ) throws HmsException {
        appointmentService.cancelAppointment(appointmentId);
        return new ResponseEntity<>("Appointment Cancelled",HttpStatus.NO_CONTENT);
    }

    @Operation(operationId = "getAppointmentDetails", summary = "Get appointment", description = "Returns appointment details by appointment id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment found",
                    content = @Content(schema = @Schema(implementation = AppointmentDTO.class))),
            @ApiResponse(responseCode = "500", description = "Business or processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/get/{appointmentId}")
    public ResponseEntity<AppointmentDTO> getAppointmentDetails(
            @Parameter(description = "Appointment id", example = "1001") @PathVariable Long appointmentId
    ) throws HmsException {
        AppointmentDTO appointmentDTO = appointmentService.getAppointmentDetails(appointmentId);
        return new ResponseEntity<>(appointmentDTO, HttpStatus.OK);
    }

    @Operation(operationId = "getAppointmentDetailsWithName", summary = "Get appointment with names", description = "Returns appointment details enriched with patient and doctor names")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment details fetched",
                    content = @Content(schema = @Schema(implementation = AppointmentDetails.class))),
            @ApiResponse(responseCode = "500", description = "Business or processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/get/details/{appointmentId}")
    public ResponseEntity<AppointmentDetails> getAppointmentDetailsWithName(
            @Parameter(description = "Appointment id", example = "1001") @PathVariable Long appointmentId
    ) throws HmsException {
        AppointmentDetails  appointmentDetails= appointmentService.getAppointmentDetailsWithName(appointmentId);
        return new ResponseEntity<>(appointmentDetails, HttpStatus.OK);
    }
    @Operation(operationId = "getAllAppointmentsByPatientId", summary = "Get patient appointments", description = "Returns all appointments for a patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments fetched",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppointmentDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Business or processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
@GetMapping("/getAllByPatient/{patientId}")
    public ResponseEntity<?> getAllAppointmentsByPatientId(
            @Parameter(description = "Patient id", example = "2001") @PathVariable Long patientId
    ) throws HmsException {
        return new ResponseEntity<>(appointmentService.getAllAppointmentsByPatientId(patientId), HttpStatus.OK);
    }
    @Operation(operationId = "getAllAppointmentsByDoctorId", summary = "Get doctor appointments", description = "Returns all appointments for a doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments fetched",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppointmentDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Business or processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
@GetMapping("/getAllByDoctor/{doctorId}")
    public ResponseEntity<?> getAllAppointmentsByDoctorId(
            @Parameter(description = "Doctor id", example = "3001") @PathVariable Long doctorId
    ) throws HmsException {
        return new ResponseEntity<>(appointmentService.getAllAppointmentsByDoctorId(doctorId), HttpStatus.OK);
    }

    @Operation(operationId = "getAppointmentCountByPatient", summary = "Monthly count by patient", description = "Returns month-wise appointment counts for a patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Counts fetched",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MonthlyVisitDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Business or processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/countByPatient/{patientId}")
    public ResponseEntity<List<MonthlyVisitDTO>> getAppointmentCountByPatient(
            @Parameter(description = "Patient id", example = "2001") @PathVariable Long patientId
    ) throws HmsException {
        return new ResponseEntity<>(appointmentService.getAppointmentCountByPatient(patientId), HttpStatus.OK);
    }
    @Operation(operationId = "getAppointmentCountByDoctor", summary = "Monthly count by doctor", description = "Returns month-wise appointment counts for a doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Counts fetched",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MonthlyVisitDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Business or processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/countByDoctor/{doctorId}")
    public ResponseEntity<List<MonthlyVisitDTO>> getAppointmentCountByDoctor(
            @Parameter(description = "Doctor id", example = "3001") @PathVariable Long doctorId
    ) throws HmsException {
        return new ResponseEntity<>(appointmentService.getAppointmentCountByDoctor(doctorId), HttpStatus.OK);
    }
    @Operation(operationId = "getAppointmentCounts", summary = "Overall monthly count", description = "Returns overall month-wise appointment counts")
    @ApiResponse(responseCode = "200", description = "Counts fetched",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = MonthlyVisitDTO.class))))
    @GetMapping("/visitCount")
    public ResponseEntity<List<MonthlyVisitDTO>> getAppointmentCounts() throws HmsException {
        return new ResponseEntity<>(appointmentService.getAppointmentCount(), HttpStatus.OK);
    }


    @Operation(operationId = "getAppointmentReasonsCountByPatient", summary = "Reason count by patient", description = "Returns reason-wise appointment counts for a patient")
    @ApiResponse(responseCode = "200", description = "Reason counts fetched",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReasonCountDTO.class))))
    @GetMapping("/countReasonsByPatient/{patientId}")
    public ResponseEntity<List<ReasonCountDTO>> getAppointmentReasonsCountByPatient(
            @Parameter(description = "Patient id", example = "2001") @PathVariable Long patientId
    ) throws HmsException {
        return new ResponseEntity<>(appointmentService.getReasonCountByPatient(patientId), HttpStatus.OK);
    }


    @Operation(operationId = "getAppointmentReasonsCountByDoctor", summary = "Reason count by doctor", description = "Returns reason-wise appointment counts for a doctor")
    @ApiResponse(responseCode = "200", description = "Reason counts fetched",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReasonCountDTO.class))))
    @GetMapping("/countReasonsByDoctor/{doctorId}")
    public ResponseEntity<List<ReasonCountDTO>> getAppointmentReasonsCountByDoctor(
            @Parameter(description = "Doctor id", example = "3001") @PathVariable Long doctorId
    ) throws HmsException {
        return new ResponseEntity<>(appointmentService.getReasonCountByDoctor(doctorId), HttpStatus.OK);
    }

    @Operation(operationId = "getAppointmentReasonsCount", summary = "Overall reason count", description = "Returns reason-wise appointment counts across all appointments")
    @ApiResponse(responseCode = "200", description = "Reason counts fetched",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReasonCountDTO.class))))
    @GetMapping("/countReasons")
    public ResponseEntity<List<ReasonCountDTO>> getAppointmentReasonsCount() throws HmsException {
        return new ResponseEntity<>(appointmentService.getReasonCount(), HttpStatus.OK);
    }


    @Operation(operationId = "getMedicinesByPatient", summary = "Medicines by patient", description = "Returns prescribed medicines for a patient")
    @ApiResponse(responseCode = "200", description = "Medicines fetched",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = MedicineDTO.class))))
    @GetMapping("/getMedicinesByPatient/{patientId}")
    public ResponseEntity<List<MedicineDTO>> getMedicinesByPatient(
            @Parameter(description = "Patient id", example = "2001") @PathVariable Long patientId
    ) throws HmsException {
        return new ResponseEntity<>(prescriptionService.getMedicineByPatientId(patientId), HttpStatus.OK);
    }

    @Operation(operationId = "getTodaysAppointments", summary = "Today's appointments", description = "Returns today's appointments with patient/doctor names")
    @ApiResponse(responseCode = "200", description = "Today's appointments fetched",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppointmentDetails.class))))
    @GetMapping("/today")
    public ResponseEntity<List<AppointmentDetails>>  getTodaysAppointments() throws HmsException {
        return new ResponseEntity<>(appointmentService.getTodaysAppointments(), HttpStatus.OK);
    }
}
