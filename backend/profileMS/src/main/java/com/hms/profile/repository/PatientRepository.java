package com.hms.profile.repository;

import com.hms.profile.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient,Long> {

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByAadharNo(String aadharNo);
}
