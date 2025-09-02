package com.hms.appointment.repository;


import com.hms.appointment.entity.ApRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ApRecordRepository extends CrudRepository<ApRecord,Long> {

    Optional<ApRecord> findByAppointment_Id(Long appointmentId);



    List<ApRecord> findByPatientId(Long patientId);
    Boolean existsByAppointment_Id(Long appointmentId);
}
