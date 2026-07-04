package com.hms.pharmacy.service;

import com.hms.pharmacy.dto.SaleDTO;
import com.hms.pharmacy.dto.SaleItemDTO;
import com.hms.pharmacy.dto.SaleRequest;
import com.hms.pharmacy.dto.event.SaleCreatedEvent;
import com.hms.pharmacy.entity.Sale;
import com.hms.pharmacy.exception.HmsException;
import com.hms.pharmacy.kafka.PharmacyEventPublisher;
import com.hms.pharmacy.repository.SaleItemRepository;
import com.hms.pharmacy.repository.SaleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {
    private static final Logger log = LoggerFactory.getLogger(SaleServiceImpl.class);

   private final MedicineInventoryService medicineInventoryService;
    private final MedicineService medicineService;
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final SaleItemService saleItemService;
    private final PharmacyEventPublisher pharmacyEventPublisher;

    @Override
    @Transactional
    public Long createSale(SaleRequest dto) throws HmsException {

        if(dto.getPrescriptionId()!=null && saleRepository.existsByPrescriptionId(dto.getPrescriptionId())){
            throw new HmsException("SALE_ALREADY_EXISTS");
        }
            for (SaleItemDTO saleItem : dto.getSaleItems()) {
                saleItem.setBatchNo(
                        medicineInventoryService.sellStock(saleItem.getMedicineId(), saleItem.getQuantity()));
            }

            Sale sale = new Sale(null, dto.getPrescriptionId(), dto.getBuyerName(), dto.getBuyerContact(), dto.getBuyerEmail(), LocalDateTime.now(), dto.getTotalAmount());
            sale = saleRepository.save(sale);
            saleItemService.createSaleItems(sale.getId(), dto.getSaleItems());
            publishSaleCreated(sale, dto);
            return sale.getId();
        }

    private void publishSaleCreated(Sale sale, SaleRequest dto) {
        try {
            if (dto.getBuyerEmail() == null || dto.getBuyerEmail().isBlank()) return;
            List<SaleCreatedEvent.SaleItemInfo> items = dto.getSaleItems() == null ? List.of()
                    : dto.getSaleItems().stream().map(item -> {
                        String name = "Medicine";
                        try {
                            name = medicineService.getMedicineById(item.getMedicineId()).getName();
                        } catch (Exception ignored) {}
                        double lineTotal = item.getUnitPrice() != null && item.getQuantity() != null
                                ? item.getUnitPrice() * item.getQuantity() : 0.0;
                        return new SaleCreatedEvent.SaleItemInfo(name, item.getBatchNo(), item.getQuantity(), item.getUnitPrice(), lineTotal);
                    }).toList();
            SaleCreatedEvent event = new SaleCreatedEvent(
                    sale.getId(), dto.getPrescriptionId(),
                    dto.getBuyerName(), dto.getBuyerEmail(), dto.getBuyerContact(),
                    sale.getSaleDate(), dto.getTotalAmount(), items);
            pharmacyEventPublisher.publishSaleCreated(event);
        } catch (Exception e) {
            log.error("Failed to publish sale-created event for saleId={}: {}", sale.getId(), e.getMessage(), e);
        }
    }

    @Override
    public void updateSale(SaleDTO dto) throws HmsException {
        Sale sale = saleRepository.findById(dto.getId()).orElseThrow(() -> new HmsException("SALE_NOT_FOUND"));
        dto.setSaleDate(dto.getSaleDate());
        dto.setTotalAmount(dto.getTotalAmount());
        saleRepository.save(dto.toEntity());
    }

    @Override
    public SaleDTO getSale(Long id) throws HmsException {


        return saleRepository.findById(id).orElseThrow(() -> new HmsException("SALE_NOT_FOUND")).toDTO();
    }

    @Override
    public SaleDTO getSaleByPrescriptionId(Long prescriptionId) throws HmsException {
        return saleRepository.findByPrescriptionId(prescriptionId).orElseThrow(() -> new HmsException("SALE_NOT_FOUND")).toDTO();
    }

    @Override
    public List<SaleDTO> getAllSales() throws HmsException {

return ((List<Sale>) saleRepository.findAll()).stream().map(Sale::toDTO).toList();
    }
}
