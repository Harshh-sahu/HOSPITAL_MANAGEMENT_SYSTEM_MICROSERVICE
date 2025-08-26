package com.hms.appointment.repository;

import com.hms.appointment.entity.Medicine;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineRepository extends CrudRepository<Medicine,Long> {


    List<Medicine> findAllByPrescription_Id(Long prescriptionId);
}
