package com.hms.pharmacy.service;

import com.hms.pharmacy.dto.SaleDTO;
import com.hms.pharmacy.dto.SaleItemDTO;
import com.hms.pharmacy.dto.SaleRequest;
import com.hms.pharmacy.entity.Sale;
import com.hms.pharmacy.exception.HmsException;
import com.hms.pharmacy.repository.SaleItemRepository;
import com.hms.pharmacy.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceImplTest {

    @Mock
    private MedicineInventoryService medicineInventoryService;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SaleItemRepository saleItemRepository;

    @Mock
    private SaleItemService saleItemService;

    @InjectMocks
    private SaleServiceImpl saleService;

    @Test
    void createSale_duplicatePrescription_throws() {
        SaleRequest req = new SaleRequest(100L, "Buyer", "999", 50.0, List.of(new SaleItemDTO(null, null, 1L, null, 1, 10.0)));
        when(saleRepository.existsByPrescriptionId(100L)).thenReturn(true);

        HmsException ex = assertThrows(HmsException.class, () -> saleService.createSale(req));

        assertEquals("SALE_ALREADY_EXISTS", ex.getMessage());
        verify(saleRepository, never()).save(any());
    }

    @Test
    void createSale_success_sellsStockAndCreatesItems() {
        SaleItemDTO item = new SaleItemDTO(null, null, 1L, null, 2, 10.0);
        SaleRequest req = new SaleRequest(100L, "Buyer", "999", 20.0, List.of(item));
        when(saleRepository.existsByPrescriptionId(100L)).thenReturn(false);
        when(medicineInventoryService.sellStock(1L, 2)).thenReturn("Batch B1: 2 units");
        when(saleRepository.save(any(Sale.class))).thenReturn(new Sale(55L, 100L, "Buyer", "999", LocalDateTime.now(), 20.0));

        Long id = saleService.createSale(req);

        assertEquals(55L, id);
        assertEquals("Batch B1: 2 units", req.getSaleItems().get(0).getBatchNo());
        verify(saleItemService).createSaleItems(eq(55L), anyList());
    }

    @Test
    void getSale_notFound_throws() {
        when(saleRepository.findById(99L)).thenReturn(Optional.empty());

        HmsException ex = assertThrows(HmsException.class, () -> saleService.getSale(99L));
        assertEquals("SALE_NOT_FOUND", ex.getMessage());
    }

    @Test
    void getSaleByPrescriptionId_returnsDto() {
        when(saleRepository.findByPrescriptionId(100L))
                .thenReturn(Optional.of(new Sale(5L, 100L, "Buyer", "999", LocalDateTime.now(), 20.0)));

        SaleDTO dto = saleService.getSaleByPrescriptionId(100L);

        assertEquals(5L, dto.getId());
        assertEquals(100L, dto.getPrescriptionId());
    }
}

