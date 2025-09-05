package com.hms.pharmacy.repository;

import com.hms.pharmacy.entity.MedicineInventory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineInventoryRepository extends CrudRepository<MedicineInventory,Long> {

}
