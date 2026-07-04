package com.EmailService.EmailServiceMS.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LowStockAlertEvent {
    private Long medicineId;
    private String medicineName;
    private Integer currentStock;
    private Integer threshold;
}
