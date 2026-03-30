package com.hms.pharmacy.dto;

import com.hms.pharmacy.entity.Medicine;
import com.hms.pharmacy.entity.MedicineCategory;
import com.hms.pharmacy.entity.MedicineType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "MedicineDTO", description = "Medicine catalog entry")
public class MedicineDTO {


    @Schema(description = "Medicine id", example = "501")
    private Long id;
    @Schema(description = "Medicine name", example = "Paracetamol")
    private String name;
    @Schema(description = "Dosage", example = "500mg")
    private String dosage;
    @Schema(description = "Category", example = "ANALGESIC")
    private MedicineCategory category;
    @Schema(description = "Type", example = "TABLET")
    private MedicineType type;
    @Schema(description = "Manufacturer", example = "ABC Pharma")
    private String manufacturer;
    @Schema(description = "Unit price", example = "15")
    private Integer unitPrice;
    @Schema(description = "Current stock count", example = "120")
    private Integer stock;
    @Schema(description = "Creation timestamp", example = "2026-03-30T14:10:00")
    private LocalDateTime createdAt;

public Medicine toEntity() {
    return new Medicine(id, name, dosage, category, type, manufacturer, unitPrice,stock, createdAt);
}
}
