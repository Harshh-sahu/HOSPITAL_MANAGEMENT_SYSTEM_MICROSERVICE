package com.hms.pharmacy.dto;

import com.hms.pharmacy.entity.Sale;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "SaleDTO", description = "Sale header information")
public class SaleDTO {
    @Schema(description = "Sale id", example = "7001")
    private Long id;
    @Schema(description = "Prescription id", example = "11001")
    private Long prescriptionId;
    @Schema(description = "Buyer name", example = "Aman Sharma")
    private String buyerName;
    @Schema(description = "Buyer contact number", example = "9876543210")
    private String buyerContact;
     @Schema(description = "Sale timestamp", example = "2026-03-30T16:30:00")
     private LocalDateTime saleDate;
     @Schema(description = "Total billed amount", example = "349.5")
     private Double totalAmount;

     public Sale toEntity(){
            return new Sale(id,prescriptionId,buyerName,buyerContact,saleDate,totalAmount);
     }




}
