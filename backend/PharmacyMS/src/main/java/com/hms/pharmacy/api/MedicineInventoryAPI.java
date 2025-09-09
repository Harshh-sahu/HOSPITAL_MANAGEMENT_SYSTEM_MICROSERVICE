package com.hms.pharmacy.api;

import com.hms.pharmacy.dto.MedicineInventoryDTO;
import com.hms.pharmacy.entity.MedicineInventory;
import com.hms.pharmacy.exception.HmsException;
import com.hms.pharmacy.service.MedicineInventoryService;
import com.hms.pharmacy.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/pharmacy/inventory")
@RequiredArgsConstructor
public class MedicineInventoryAPI {


    private final MedicineInventoryService  medicineInventoryService;



    @PostMapping("/add")
    public ResponseEntity<MedicineInventoryDTO> addMedicineToInventory(@RequestBody MedicineInventoryDTO medicineInventoryDTO) throws HmsException {
        return new ResponseEntity<>(medicineInventoryService.addMedicine(medicineInventoryDTO), HttpStatus.CREATED);
    }


    @PutMapping("/update")
    public ResponseEntity<MedicineInventoryDTO> updateMedicineInInventory(@RequestBody MedicineInventoryDTO medicineInventoryDTO) throws HmsException {
        return ResponseEntity.ok(medicineInventoryService.updateMedicine(medicineInventoryDTO.getId(), medicineInventoryDTO));
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<MedicineInventoryDTO> getMedicineFromInventoryById(@PathVariable Long id) throws HmsException {
        return ResponseEntity.ok(medicineInventoryService.getMedicineById(id));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<MedicineInventoryDTO>> getAllMedicinesFromInventory() throws HmsException {
        return ResponseEntity.ok(medicineInventoryService.getAllMedicines());
    }
}
