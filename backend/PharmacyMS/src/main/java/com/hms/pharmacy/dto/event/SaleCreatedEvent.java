package com.hms.pharmacy.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleCreatedEvent {
    private Long saleId;
    private Long prescriptionId;
    private String buyerName;
    private String buyerEmail;
    private String buyerContact;
    private LocalDateTime saleDate;
    private Double totalAmount;
    private List<SaleItemInfo> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleItemInfo {
        private String medicineName;
        private String batchNo;
        private Integer quantity;
        private Double unitPrice;
        private Double lineTotal;
    }
}
