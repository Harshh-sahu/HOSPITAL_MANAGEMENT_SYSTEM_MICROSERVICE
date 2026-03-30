package com.hms.pharmacy.dto;


import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SaleRequest", description = "Request payload for creating a sale with line items")
public class SaleRequest {


    @Schema(description = "Prescription id", example = "11001")
    private Long prescriptionId;
    @Schema(description = "Buyer name", example = "Aman Sharma")
    private String buyerName;
    @Schema(description = "Buyer contact number", example = "9876543210")
    private String buyerContact;
    @Schema(description = "Total billed amount", example = "349.5")
    private Double totalAmount;
    @ArraySchema(schema = @Schema(implementation = SaleItemDTO.class), arraySchema = @Schema(description = "Sale line items"))
    private List<SaleItemDTO> saleItems;
}
