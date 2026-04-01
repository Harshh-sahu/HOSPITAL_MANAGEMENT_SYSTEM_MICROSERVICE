package com.hms.pharmacy.service;

import com.hms.pharmacy.dto.SaleItemDTO;
import com.hms.pharmacy.entity.Medicine;
import com.hms.pharmacy.entity.Sale;
import com.hms.pharmacy.entity.SaleItem;
import com.hms.pharmacy.exception.HmsException;
import com.hms.pharmacy.repository.SaleItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceItemImplTest {

    @Mock
    private MedicineInventoryService medicineInventoryService;

    @Mock
    private SaleItemRepository saleItemRepository;

    @InjectMocks
    private SaleServiceItemImpl saleItemService;

    @Test
    void createSaleItem_returnsId() {
        SaleItemDTO dto = new SaleItemDTO(null, 1L, 2L, "B1", 1, 10.0);
        when(saleItemRepository.save(any(SaleItem.class))).thenReturn(new SaleItem(99L, new Sale(1L), new Medicine(2L), "B1", 1, 10.0));

        Long id = saleItemService.createSaleItem(dto);

        assertEquals(99L, id);
    }

    @Test
    void updateSaleItem_notFound_throws() {
        when(saleItemRepository.findById(11L)).thenReturn(Optional.empty());

        HmsException ex = assertThrows(HmsException.class, () -> saleItemService.updateSaleItem(new SaleItemDTO(11L, 1L, 2L, "B1", 2, 10.0)));
        assertEquals("SALE_ITEM_NOT_FOUND", ex.getMessage());
    }

    @Test
    void getSaleItemsBySaleId_mapsDtos() {
        when(saleItemRepository.findBySaleId(1L)).thenReturn(List.of(
                new SaleItem(1L, new Sale(1L), new Medicine(2L), "B1", 2, 10.0)
        ));

        List<SaleItemDTO> list = saleItemService.getSaleItemsBySaleId(1L);

        assertEquals(1, list.size());
        assertEquals("B1", list.get(0).getBatchNo());
    }

    @Test
    void createSaleItems_savesAllWithSaleId() {
        List<SaleItemDTO> list = List.of(
                new SaleItemDTO(null, null, 2L, "B1", 1, 10.0),
                new SaleItemDTO(null, null, 3L, "B2", 2, 20.0)
        );

        saleItemService.createSaleItems(50L, list);

        verify(saleItemRepository, times(2)).save(any(SaleItem.class));
    }

    @Test
    void createMultipleSaleItem_savesAllWithSaleAndMedicine() {
        List<SaleItemDTO> list = List.of(
                new SaleItemDTO(null, null, null, "B1", 1, 10.0)
        );

        saleItemService.createMuiltipleSaleItem(50L, 2L, list);

        verify(saleItemRepository).save(any(SaleItem.class));
    }

    @Test
    void updateSaleItem_success_updatesValues() {
        SaleItem item = new SaleItem(11L, new Sale(1L), new Medicine(2L), "B1", 1, 10.0);
        when(saleItemRepository.findById(11L)).thenReturn(Optional.of(item));

        saleItemService.updateSaleItem(new SaleItemDTO(11L, 1L, 2L, "B1", 3, 12.5));

        assertEquals(3, item.getQuantity());
        assertEquals(12.5, item.getUnitPrice());
        verify(saleItemRepository).save(item);
    }

    @Test
    void getSaleItem_notFound_throws() {
        when(saleItemRepository.findById(99L)).thenReturn(Optional.empty());

        HmsException ex = assertThrows(HmsException.class, () -> saleItemService.getSaleItem(99L));
        assertEquals("SALE_ITEM_NOT_FOUND", ex.getMessage());
    }
}
