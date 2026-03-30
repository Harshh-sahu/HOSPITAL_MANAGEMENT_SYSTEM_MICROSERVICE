package com.hms.pharmacy.api;

import com.hms.pharmacy.dto.MedicineInventoryDTO;
import com.hms.pharmacy.exception.HmsException;
import com.hms.pharmacy.service.MedicineInventoryService;
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

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/pharmacy/inventory")
@RequiredArgsConstructor
@Validated
@Tag(name = "Pharmacy Inventory APIs", description = "Inventory and stock operations")
@SecurityRequirement(name = "X-Secret-Key")
public class MedicineInventoryAPI {


    private final MedicineInventoryService  medicineInventoryService;


    @Operation(operationId = "addMedicineToInventory", summary = "Add medicine to inventory", description = "Adds a medicine batch to inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventory created",
                    content = @Content(schema = @Schema(implementation = MedicineInventoryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping("/add")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Inventory payload",
            content = @Content(schema = @Schema(implementation = MedicineInventoryDTO.class)))
    public ResponseEntity<MedicineInventoryDTO> addMedicineToInventory(@Valid @RequestBody MedicineInventoryDTO medicineInventoryDTO) throws HmsException {
        return new ResponseEntity<>(medicineInventoryService.addMedicine(medicineInventoryDTO), HttpStatus.CREATED);
    }

    @Operation(operationId = "updateMedicineInInventory", summary = "Update inventory record", description = "Updates quantity and metadata for inventory record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated",
                    content = @Content(schema = @Schema(implementation = MedicineInventoryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PutMapping("/update")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Inventory payload",
            content = @Content(schema = @Schema(implementation = MedicineInventoryDTO.class)))
    public ResponseEntity<MedicineInventoryDTO> updateMedicineInInventory(@Valid @RequestBody MedicineInventoryDTO medicineInventoryDTO) throws HmsException {
        return ResponseEntity.ok(medicineInventoryService.updateMedicine(medicineInventoryDTO.getId(), medicineInventoryDTO));
    }

    @Operation(operationId = "getMedicineFromInventoryById", summary = "Get inventory by id", description = "Returns inventory details for given inventory id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory fetched",
                    content = @Content(schema = @Schema(implementation = MedicineInventoryDTO.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<MedicineInventoryDTO> getMedicineFromInventoryById(
            @Parameter(description = "Inventory id", example = "9001") @PathVariable Long id
    ) throws HmsException {
        return ResponseEntity.ok(medicineInventoryService.getMedicineById(id));
    }

    @Operation(operationId = "getAllMedicinesFromInventory", summary = "Get all inventory records", description = "Returns all inventory records")
    @ApiResponse(responseCode = "200", description = "Inventory list fetched",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = MedicineInventoryDTO.class))))
    @GetMapping("/getAll")
    public ResponseEntity<List<MedicineInventoryDTO>> getAllMedicinesFromInventory() throws HmsException {
        return ResponseEntity.ok(medicineInventoryService.getAllMedicines());
    }
}
