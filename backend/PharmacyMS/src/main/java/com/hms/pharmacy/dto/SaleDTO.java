package com.hms.pharmacy.dto;

import com.hms.pharmacy.entity.Sale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleDTO {
    private Long id;
    private Long prescriptionId;
    private String buyerName;
    private String buyerContact;
     private LocalDateTime saleDate;
     private Double totalAmount;

     public Sale toEntity(){
            return new Sale(id,prescriptionId,buyerName,buyerContact,saleDate,totalAmount);
     }




}
