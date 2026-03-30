package com.hms.pharmacy.dto;

import com.hms.pharmacy.entity.Medicine;
import com.hms.pharmacy.entity.MedicineInventory;
import com.hms.pharmacy.entity.StockStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "MedicineInventoryDTO", description = "Inventory details for a medicine batch")
public class MedicineInventoryDTO {


    @Schema(description = "Inventory id", example = "9001")
    private Long id;


    @Schema(description = "Medicine id", example = "501")
    private Long medicineId;
    @Schema(description = "Batch number", example = "BATCH-2026-001")
    private String batchNo;
    @Schema(description = "Available quantity", example = "85")
    private Integer quantity;
    @Schema(description = "Expiry date", example = "2027-09-30")
    private LocalDate expiryDate;
    @Schema(description = "Date added to inventory", example = "2026-03-30")
    private LocalDate addedDate;
    @Schema(description = "Initial quantity at stock-in", example = "100")
    private Integer initialQuantity;
    @Schema(description = "Stock status", example = "IN_STOCK")
    private StockStatus status;
    public MedicineInventory toEntity(){
        return new MedicineInventory(id,new Medicine(medicineId),batchNo,quantity,expiryDate,addedDate,initialQuantity,status);
    }
}
