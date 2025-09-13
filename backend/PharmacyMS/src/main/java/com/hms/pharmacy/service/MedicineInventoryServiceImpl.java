package com.hms.pharmacy.service;

import com.hms.pharmacy.dto.MedicineInventoryDTO;
import com.hms.pharmacy.entity.MedicineInventory;
import com.hms.pharmacy.entity.StockStatus;
import com.hms.pharmacy.exception.HmsException;
import com.hms.pharmacy.repository.MedicineInventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicineInventoryServiceImpl implements MedicineInventoryService {
    private final MedicineService medicineService;
    private final MedicineInventoryRepository medicineInventoryRepository;


    @Override
    public List<MedicineInventoryDTO> getAllMedicines() throws HmsException {

        List<MedicineInventory> inventories = (List<MedicineInventory>) medicineInventoryRepository.findAll();
        return inventories.stream().map(MedicineInventory::toDTO).toList();

    }

    @Override
    public MedicineInventoryDTO getMedicineById(Long id) throws HmsException {
        return medicineInventoryRepository.findById(id).orElseThrow(() -> new HmsException("INVENTORY_NOT_FOUND")).toDTO();
    }

    @Override
    public MedicineInventoryDTO addMedicine(MedicineInventoryDTO medicine) throws HmsException {

        medicine.setAddedDate(LocalDate.now());
        medicineService.addStock(medicine.getMedicineId(), medicine.getQuantity());
        medicine.setInitialQuantity(medicine.getQuantity());
        medicine.setStatus(StockStatus.ACTIVE);
        return medicineInventoryRepository.save(medicine.toEntity()).toDTO();


    }

    @Override
    public MedicineInventoryDTO updateMedicine(Long id, MedicineInventoryDTO medicine) throws HmsException {

        MedicineInventory existingInventory = medicineInventoryRepository.findById(medicine.getId()).orElseThrow(() -> new HmsException("INVENTORY_NOT_FOUND"));
        existingInventory.setBatchNo(medicine.getBatchNo());
        if (existingInventory.getQuantity() > medicine.getQuantity()) {
            medicineService.addStock(medicine.getMedicineId(), medicine.getQuantity() - existingInventory.getQuantity());
        } else if (existingInventory.getQuantity() > medicine.getQuantity()) {
            medicineService.removeStock(medicine.getMedicineId(), existingInventory.getQuantity() - medicine.getQuantity());
        }
        existingInventory.setQuantity(medicine.getQuantity());
        existingInventory.setInitialQuantity(medicine.getQuantity());
        existingInventory.setExpiryDate(medicine.getExpiryDate());
        return medicineInventoryRepository.save(existingInventory).toDTO();
    }

    @Override
    public void deleteMedicine(Long id) throws HmsException {
        medicineInventoryRepository.deleteById(id);
    }

   private void markExpired(List<MedicineInventory> inventories)throws  HmsException{
        for(MedicineInventory inventory:inventories){
                inventory.setStatus(StockStatus.EXPIRED);

            }
            medicineInventoryRepository.saveAll(inventories);
        }

    @Override
    @Scheduled(cron = "0 27 22 * * ?")
    public void deleteExpiredMedicines() throws HmsException {
        System.out.println("delete expired medicine task running...");

        List<MedicineInventory> expiredMedicines = medicineInventoryRepository.findByExpiryDateBefore(LocalDate.now());
        for (MedicineInventory medicine : expiredMedicines) {
            medicineService.removeStock(medicine.getMedicine().getId(), medicine.getQuantity());
        }
this.markExpired(expiredMedicines);    }


}
