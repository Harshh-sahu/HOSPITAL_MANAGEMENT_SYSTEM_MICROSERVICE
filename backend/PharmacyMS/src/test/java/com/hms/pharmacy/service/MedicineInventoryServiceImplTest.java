package com.hms.pharmacy.service;

import com.hms.pharmacy.dto.MedicineInventoryDTO;
import com.hms.pharmacy.entity.Medicine;
import com.hms.pharmacy.entity.MedicineInventory;
import com.hms.pharmacy.entity.StockStatus;
import com.hms.pharmacy.exception.HmsException;
import com.hms.pharmacy.repository.MedicineInventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicineInventoryServiceImplTest {

    @Mock
    private MedicineService medicineService;

    @Mock
    private MedicineInventoryRepository medicineInventoryRepository;

    @InjectMocks
    private MedicineInventoryServiceImpl inventoryService;

    @Test
    void addMedicine_setsFieldsAndAddsStock() {
        MedicineInventoryDTO dto = new MedicineInventoryDTO(null, 1L, "B1", 10, LocalDate.now().plusDays(10), null, null, null);
        when(medicineInventoryRepository.save(any(MedicineInventory.class)))
                .thenReturn(new MedicineInventory(100L, new Medicine(1L), "B1", 10, LocalDate.now().plusDays(10), LocalDate.now(), 10, StockStatus.ACTIVE));

        MedicineInventoryDTO saved = inventoryService.addMedicine(dto);

        assertEquals(100L, saved.getId());
        assertEquals(10, saved.getInitialQuantity());
        assertEquals(StockStatus.ACTIVE, saved.getStatus());
        assertNotNull(saved.getAddedDate());
        verify(medicineService).addStock(1L, 10);
    }

    @Test
    void updateMedicine_increaseQuantity_addsStockDifference() {
        MedicineInventory existing = new MedicineInventory(1L, new Medicine(1L), "B1", 10, LocalDate.now().plusDays(10), LocalDate.now(), 10, StockStatus.ACTIVE);
        when(medicineInventoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(medicineInventoryRepository.save(any(MedicineInventory.class))).thenReturn(existing);

        MedicineInventoryDTO dto = new MedicineInventoryDTO(1L, 1L, "B2", 15, LocalDate.now().plusDays(20), LocalDate.now(), 15, StockStatus.ACTIVE);
        inventoryService.updateMedicine(1L, dto);

        verify(medicineService).addStock(1L, 5);
        verify(medicineService, never()).removeStock(anyLong(), anyInt());
    }

    @Test
    void updateMedicine_decreaseQuantity_removesStockDifference() {
        MedicineInventory existing = new MedicineInventory(1L, new Medicine(1L), "B1", 10, LocalDate.now().plusDays(10), LocalDate.now(), 10, StockStatus.ACTIVE);
        when(medicineInventoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(medicineInventoryRepository.save(any(MedicineInventory.class))).thenReturn(existing);

        MedicineInventoryDTO dto = new MedicineInventoryDTO(1L, 1L, "B2", 6, LocalDate.now().plusDays(20), LocalDate.now(), 6, StockStatus.ACTIVE);
        inventoryService.updateMedicine(1L, dto);

        verify(medicineService).removeStock(1L, 4);
    }

    @Test
    void sellStock_outOfStock_throws() {
        when(medicineInventoryRepository.findByMedicineIdAndExpiryDateAfterAndQuantityGreaterThanAndStatusOrderByExpiryDateAsc(
                eq(1L), any(LocalDate.class), eq(0), eq(StockStatus.ACTIVE)))
                .thenReturn(List.of());

        HmsException ex = assertThrows(HmsException.class, () -> inventoryService.sellStock(1L, 5));

        assertEquals("OUT_OF_STOCK", ex.getMessage());
    }

    @Test
    void sellStock_insufficient_throws() {
        MedicineInventory inv = new MedicineInventory(1L, new Medicine(1L), "B1", 2, LocalDate.now().plusDays(10), LocalDate.now(), 2, StockStatus.ACTIVE);
        when(medicineInventoryRepository.findByMedicineIdAndExpiryDateAfterAndQuantityGreaterThanAndStatusOrderByExpiryDateAsc(
                eq(1L), any(LocalDate.class), eq(0), eq(StockStatus.ACTIVE)))
                .thenReturn(List.of(inv));

        HmsException ex = assertThrows(HmsException.class, () -> inventoryService.sellStock(1L, 5));

        assertEquals("INSUFFICIENT_STOCK", ex.getMessage());
        verify(medicineService, never()).removeStock(anyLong(), anyInt());
    }

    @Test
    void sellStock_success_updatesStockAndReturnsBatchDetails() {
        MedicineInventory inv1 = new MedicineInventory(1L, new Medicine(1L), "B1", 2, LocalDate.now().plusDays(10), LocalDate.now(), 2, StockStatus.ACTIVE);
        MedicineInventory inv2 = new MedicineInventory(2L, new Medicine(1L), "B2", 5, LocalDate.now().plusDays(20), LocalDate.now(), 5, StockStatus.ACTIVE);
        when(medicineInventoryRepository.findByMedicineIdAndExpiryDateAfterAndQuantityGreaterThanAndStatusOrderByExpiryDateAsc(
                eq(1L), any(LocalDate.class), eq(0), eq(StockStatus.ACTIVE)))
                .thenReturn(List.of(inv1, inv2));

        String details = inventoryService.sellStock(1L, 4);

        assertTrue(details.contains("Batch B1: 2 units"));
        assertTrue(details.contains("Batch B2: 2 units"));
        verify(medicineService).removeStock(1L, 4);
        verify(medicineInventoryRepository).saveAll(anyList());
    }

    @Test
    void getMedicineById_notFound_throws() {
        when(medicineInventoryRepository.findById(77L)).thenReturn(Optional.empty());

        HmsException ex = assertThrows(HmsException.class, () -> inventoryService.getMedicineById(77L));
        assertEquals("INVENTORY_NOT_FOUND", ex.getMessage());
    }

    @Test
    void getAllMedicines_mapsList() {
        when(medicineInventoryRepository.findAll()).thenReturn(List.of(
                new MedicineInventory(1L, new Medicine(1L), "B1", 2, LocalDate.now().plusDays(1), LocalDate.now(), 2, StockStatus.ACTIVE)
        ));

        List<MedicineInventoryDTO> list = inventoryService.getAllMedicines();

        assertEquals(1, list.size());
        assertEquals("B1", list.get(0).getBatchNo());
    }

    @Test
    void deleteMedicine_callsRepository() {
        inventoryService.deleteMedicine(10L);
        verify(medicineInventoryRepository).deleteById(10L);
    }

    @Test
    void deleteExpiredMedicines_marksAndRemovesStock() {
        MedicineInventory expired = new MedicineInventory(1L, new Medicine(1L), "B1", 3, LocalDate.now().minusDays(1), LocalDate.now(), 3, StockStatus.ACTIVE);
        when(medicineInventoryRepository.findByExpiryDateBefore(any(LocalDate.class))).thenReturn(List.of(expired));

        inventoryService.deleteExpiredMedicines();

        verify(medicineService).removeStock(1L, 3);
        verify(medicineInventoryRepository).saveAll(anyList());
        assertEquals(StockStatus.EXPIRED, expired.getStatus());
    }
}
