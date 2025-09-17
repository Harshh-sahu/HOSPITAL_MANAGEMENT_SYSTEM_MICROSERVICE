package com.hms.pharmacy.repository;

import com.hms.pharmacy.entity.MedicineInventory;
import com.hms.pharmacy.entity.StockStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicineInventoryRepository extends CrudRepository<MedicineInventory,Long> {

    List<MedicineInventory> findByExpiryDateBefore(LocalDate date);


    List<MedicineInventory> findByMedicineIdAndExpiryDateAfterAndQuantityGreaterThanAndStatusOrderByExpiryDateAsc(Long medicineId, LocalDate date, Integer quantity, StockStatus status);



}
