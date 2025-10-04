package com.hms.profile.repository;

import com.hms.profile.dto.DoctorDropdown;
import com.hms.profile.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient,Long> {

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByAadharNo(String aadharNo);

    @Query("SELECT d.id AS id,d.name AS name FROM Patient  d WHERE d.id in  ?1")
    List<DoctorDropdown> findAllPatientsDropdownsByIds(List<Long> ids);
}
