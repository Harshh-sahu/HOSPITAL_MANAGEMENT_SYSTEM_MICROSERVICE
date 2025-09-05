package com.hms.pharmacy.api;

import com.hms.pharmacy.dto.MedicineDTO;
import com.hms.pharmacy.dto.ResponseDTO;
import com.hms.pharmacy.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/pharmacy/medicines")
@RequiredArgsConstructor
public class MedicineAPI {
    private final MedicineService medicineService;

    @PostMapping("/add")
    public ResponseEntity<Long> addMedicine(@RequestBody MedicineDTO medicineDTO) throws Exception {
   return new ResponseEntity<>(medicineService.addMedicine(medicineDTO),org.springframework.http.HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<MedicineDTO> getMedicineById(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(medicineService.getMedicineById(id));
    }


    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> updateMedicine(@RequestBody MedicineDTO medicineDTO) throws Exception {
        
        medicineService.updateMedicine(medicineDTO);
        return new ResponseEntity<>(new ResponseDTO("Medicine Updated !"),HttpStatus.OK);
    }
    @GetMapping("/getAll")
    public ResponseEntity<java.util.List<MedicineDTO>> getAllMedicines() throws Exception {
        return ResponseEntity.ok(medicineService.getAllMedicines());
    }
}
