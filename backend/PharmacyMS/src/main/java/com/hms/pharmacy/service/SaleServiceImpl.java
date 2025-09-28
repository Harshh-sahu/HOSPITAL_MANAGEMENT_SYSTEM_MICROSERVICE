package com.hms.pharmacy.service;

import com.hms.pharmacy.dto.SaleDTO;
import com.hms.pharmacy.dto.SaleItemDTO;
import com.hms.pharmacy.dto.SaleRequest;
import com.hms.pharmacy.entity.Sale;
import com.hms.pharmacy.exception.HmsException;
import com.hms.pharmacy.repository.SaleItemRepository;
import com.hms.pharmacy.repository.SaleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {
   private final MedicineInventoryService medicineInventoryService;
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final SaleItemService saleItemService;

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

            Sale sale = new Sale(null, dto.getPrescriptionId(), dto.getBuyerName(), dto.getBuyerContact(), LocalDateTime.now(), dto.getTotalAmount());
            sale = saleRepository.save(sale);
            saleItemService.createSaleItems(sale.getId(), dto.getSaleItems());
            return sale.getId();
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
