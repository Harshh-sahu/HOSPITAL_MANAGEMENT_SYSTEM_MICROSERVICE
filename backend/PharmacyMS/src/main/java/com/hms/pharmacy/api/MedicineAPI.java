package com.hms.pharmacy.api;

import com.hms.pharmacy.dto.MedicineDTO;
import com.hms.pharmacy.dto.ResponseDTO;
import com.hms.pharmacy.service.MedicineService;
import com.hms.pharmacy.utility.ErrorInfo;
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

@RestController
@CrossOrigin
@RequestMapping("/pharmacy/medicines")
@RequiredArgsConstructor
@Validated
@Tag(name = "Pharmacy Medicine APIs", description = "Medicine catalog management operations")
@SecurityRequirement(name = "X-Secret-Key")
public class MedicineAPI {
    private final MedicineService medicineService;

    @Operation(operationId = "addMedicine", summary = "Add medicine", description = "Adds a new medicine in catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Medicine added",
                    content = @Content(schema = @Schema(type = "integer", format = "int64", example = "501"))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping("/add")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Medicine payload",
            content = @Content(schema = @Schema(implementation = MedicineDTO.class)))
    public ResponseEntity<Long> addMedicine(@Valid @RequestBody MedicineDTO medicineDTO) {
   return new ResponseEntity<>(medicineService.addMedicine(medicineDTO),org.springframework.http.HttpStatus.CREATED);
    }

    @Operation(operationId = "getMedicineById", summary = "Get medicine by id", description = "Returns medicine details by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicine fetched",
                    content = @Content(schema = @Schema(implementation = MedicineDTO.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<MedicineDTO> getMedicineById(
            @Parameter(description = "Medicine id", example = "501") @PathVariable Long id
    ) {
        return ResponseEntity.ok(medicineService.getMedicineById(id));
    }


    @Operation(operationId = "updateMedicine", summary = "Update medicine", description = "Updates an existing medicine")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicine updated",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PutMapping("/update")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Medicine payload",
            content = @Content(schema = @Schema(implementation = MedicineDTO.class)))
    public ResponseEntity<ResponseDTO> updateMedicine(@Valid @RequestBody MedicineDTO medicineDTO) {

        medicineService.updateMedicine(medicineDTO);
        return new ResponseEntity<>(new ResponseDTO("Medicine Updated !"),HttpStatus.OK);
    }

    @Operation(operationId = "getAllMedicines", summary = "Get all medicines", description = "Returns all medicines in the catalog")
    @ApiResponse(responseCode = "200", description = "Medicines fetched",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = MedicineDTO.class))))
    @GetMapping("/getAll")
    public ResponseEntity<java.util.List<MedicineDTO>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.getAllMedicines());
    }
}
