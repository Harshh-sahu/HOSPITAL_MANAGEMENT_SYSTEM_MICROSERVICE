package com.hms.appointment.api;

import com.hms.appointment.dto.ApRecordDTO;
import com.hms.appointment.dto.MedicineDTO;
import com.hms.appointment.dto.PrescriptionDetails;
import com.hms.appointment.dto.RecordDetails;
import com.hms.appointment.exception.ErrorInfo;
import com.hms.appointment.exception.HmsException;
import com.hms.appointment.service.ApRecordService;
import com.hms.appointment.service.MedicineService;
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
@Tag(name = "Appointment Report APIs", description = "Operations for appointment reports, prescriptions and medicines")
@SecurityRequirement(name = "X-Secret-Key")
public class ApRecordAPI {
    private final PrescriptionService prescriptionService;

    private final ApRecordService apRecordService;
    private final MedicineService medicineService;

    @Operation(
            operationId = "createAppointmentReport",
            summary = "Create appointment report",
            description = "Creates appointment report and linked prescription data"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Report created",
                    content = @Content(schema = @Schema(type = "integer", format = "int64", example = "5001"))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business/processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping("/create")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Appointment report payload",
            content = @Content(schema = @Schema(implementation = ApRecordDTO.class))
    )
    public ResponseEntity<Long> createAppointmentReport(@Valid @RequestBody ApRecordDTO request) throws HmsException {
        return new ResponseEntity<>(apRecordService.createApRecord(request), HttpStatus.CREATED);
    }

    @Operation(
            operationId = "updateAppointmentReport",
            summary = "Update appointment report",
            description = "Updates an existing appointment report"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report updated",
                    content = @Content(schema = @Schema(type = "string", example = "Appointment Report Updated"))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business/processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PutMapping("/update")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Appointment report payload",
            content = @Content(schema = @Schema(implementation = ApRecordDTO.class))
    )
    public ResponseEntity<String> updateAppointmentReport(@Valid @RequestBody ApRecordDTO request) throws HmsException {
        apRecordService.updateApRecord(request);
        return new ResponseEntity<>("Appointment Report Updated", HttpStatus.OK);
    }

    @Operation(
            operationId = "getAppointmentReportByAppointmentId",
            summary = "Get report by appointment id",
            description = "Returns appointment report for the given appointment id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report found",
                    content = @Content(schema = @Schema(implementation = ApRecordDTO.class))),
            @ApiResponse(responseCode = "500", description = "Business/processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/getByAppointmentId/{appointmentId}")
    public ResponseEntity<ApRecordDTO> getAppointmentReportByAppointmentId(
            @Parameter(description = "Appointment id", example = "1001") @PathVariable Long appointmentId
    ) throws HmsException {
        return new ResponseEntity<>(apRecordService.getApRecordByAppointmentId(appointmentId), HttpStatus.OK);
    }

    @Operation(
            operationId = "getAppointmentReportById",
            summary = "Get report by record id",
            description = "Returns appointment report for the given record id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report found",
                    content = @Content(schema = @Schema(implementation = ApRecordDTO.class))),
            @ApiResponse(responseCode = "500", description = "Business/processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/getById/{recordId}")
    public ResponseEntity<ApRecordDTO> getAppointmentReportById(
            @Parameter(description = "Record id", example = "5001") @PathVariable Long recordId
    ) throws HmsException {
        return new ResponseEntity<>(apRecordService.getApRecordById(recordId), HttpStatus.OK);
    }

    @Operation(
            operationId = "getAppointmentReportDetailsByAppointmentId",
            summary = "Get report details by appointment id",
            description = "Returns enriched appointment report details for the given appointment id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report details found",
                    content = @Content(schema = @Schema(implementation = ApRecordDTO.class))),
            @ApiResponse(responseCode = "500", description = "Business/processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/getDetailsByAppointmentId/{appointmentId}")
    public ResponseEntity<ApRecordDTO> getAppointmentReportDeatilsByAppointmentId(
            @Parameter(description = "Appointment id", example = "1001") @PathVariable Long appointmentId
    ) throws HmsException {
        return new ResponseEntity<>(apRecordService.getApRecordDetailsByAppointmentId(appointmentId), HttpStatus.OK);
    }

    @Operation(
            operationId = "getRecordsByPatientId",
            summary = "Get records by patient",
            description = "Returns all appointment records for a patient"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Records fetched",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RecordDetails.class)))),
            @ApiResponse(responseCode = "500", description = "Business/processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/getRecordsByPatientId/{patientId}")
    public ResponseEntity<List<RecordDetails>> getRecordsByPatientId(
            @Parameter(description = "Patient id", example = "2001") @PathVariable Long patientId
    ) throws HmsException {
        return new ResponseEntity<>(apRecordService.getRecordByPatientId(patientId), HttpStatus.OK);
    }

    @Operation(
            operationId = "isRecordExists",
            summary = "Check report existence",
            description = "Returns whether a report exists for the given appointment id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Existence check result",
                    content = @Content(schema = @Schema(type = "boolean", example = "true"))),
            @ApiResponse(responseCode = "500", description = "Business/processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/isRecordExists/{appointmentId}")
    public ResponseEntity<Boolean> isRecordExists(
            @Parameter(description = "Appointment id", example = "1001") @PathVariable Long appointmentId
    ) throws HmsException {
        return new ResponseEntity<>(apRecordService.isRecordExists(appointmentId), HttpStatus.OK);
    }

    @Operation(
            operationId = "getPrescriptionsByPatientId",
            summary = "Get prescriptions by patient",
            description = "Returns all prescriptions for a patient"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescriptions fetched",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrescriptionDetails.class)))),
            @ApiResponse(responseCode = "500", description = "Business/processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/getPrescriptionsByPatientId/{patientId}")
    public ResponseEntity<List<PrescriptionDetails>> getPrescriptionsByPatientId(
            @Parameter(description = "Patient id", example = "2001") @PathVariable Long patientId
    ) throws HmsException {
        return new ResponseEntity<>(prescriptionService.getPrescriptionsByPatientId(patientId), HttpStatus.OK);
    }

    @Operation(
            operationId = "getAllPrescriptions",
            summary = "Get all prescriptions",
            description = "Returns all prescriptions across patients"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescriptions fetched",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrescriptionDetails.class)))),
            @ApiResponse(responseCode = "500", description = "Business/processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/getAllPrescriptions")
    public ResponseEntity<List<PrescriptionDetails>> getAllPrescriptions() throws HmsException {
        return new ResponseEntity<>(prescriptionService.getPrescriptions(), HttpStatus.OK);
    }

    @Operation(
            operationId = "getMedicinesByPrescriptionId",
            summary = "Get medicines by prescription",
            description = "Returns all medicines for the given prescription id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicines fetched",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MedicineDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Business/processing error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/getMedicinesByPrescriptionId/{prescriptionId}")
    public ResponseEntity<List<MedicineDTO>> getMedicinesByPrescriptionId(
            @Parameter(description = "Prescription id", example = "4001") @PathVariable Long prescriptionId
    ) throws HmsException {
        return new ResponseEntity<>(medicineService.getAllMedicinesByPrescriptionId(prescriptionId), HttpStatus.OK);
    }

}
