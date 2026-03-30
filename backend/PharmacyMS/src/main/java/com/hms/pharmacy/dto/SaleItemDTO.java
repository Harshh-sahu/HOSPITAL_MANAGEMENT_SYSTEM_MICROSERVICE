package com.hms.pharmacy.dto;

import com.hms.pharmacy.entity.Medicine;
import com.hms.pharmacy.entity.Sale;
import com.hms.pharmacy.entity.SaleItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "SaleItemDTO", description = "Item-level details in a sale")
public class SaleItemDTO {

    @Schema(description = "Sale item id", example = "1")
    private Long id;

    @Schema(description = "Sale id", example = "7001")
    private Long saleId;
    @Schema(description = "Medicine id", example = "501")
    private Long medicineId;
    @Schema(description = "Batch number", example = "BATCH-2026-001")
    private String batchNo;
    @Schema(description = "Quantity sold", example = "2")
    private Integer quantity;
    @Schema(description = "Unit price at sale time", example = "15.5")
    private Double unitPrice;


    public SaleItem toEntity(){
        return new SaleItem(id,new Sale(saleId),new Medicine(medicineId),batchNo,quantity,unitPrice);
    }
}
