package com.hms.pharmacy.dto;

import com.hms.pharmacy.entity.MedicineCategory;
import com.hms.pharmacy.entity.MedicineType;
import com.hms.pharmacy.entity.StockStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DtoMappingTest {

    @Test
    void saleDto_toEntity_mapsFields() {
        SaleDTO dto = new SaleDTO(1L, 101L, "Buyer", "999", LocalDateTime.now(), 22.5);
        var entity = dto.toEntity();
        assertEquals(1L, entity.getId());
        assertEquals(101L, entity.getPrescriptionId());
    }

    @Test
    void medicineDto_toEntity_mapsFields() {
        MedicineDTO dto = new MedicineDTO(1L, "Paracetamol", "500mg", MedicineCategory.ANALGESIC, MedicineType.TABLET, "ABC", 10, 5, LocalDateTime.now());
        var entity = dto.toEntity();
        assertEquals("Paracetamol", entity.getName());
        assertEquals("500mg", entity.getDosage());
    }

    @Test
    void inventoryDto_toEntity_mapsFields() {
        MedicineInventoryDTO dto = new MedicineInventoryDTO(1L, 2L, "B1", 20, LocalDate.now().plusDays(10), LocalDate.now(), 20, StockStatus.ACTIVE);
        var entity = dto.toEntity();
        assertEquals(2L, entity.getMedicine().getId());
        assertEquals("B1", entity.getBatchNo());
    }

    @Test
    void saleItemDto_toEntity_mapsFields() {
        SaleItemDTO dto = new SaleItemDTO(1L, 3L, 4L, "B2", 2, 10.0);
        var entity = dto.toEntity();
        assertEquals(3L, entity.getSale().getId());
        assertEquals(4L, entity.getMedicine().getId());
    }

    @Test
    void saleRequest_constructor_mapsFields() {
        SaleRequest request = new SaleRequest(10L, "Buyer", "999", 33.0, List.of());
        assertEquals(10L, request.getPrescriptionId());
        assertEquals("Buyer", request.getBuyerName());
    }
}

