package com.hms.profile.repository;

import com.hms.profile.dto.DoctorDropdown;
import com.hms.profile.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {


    Optional<Doctor>    findByEmail(String email);

    Optional<Doctor> findByLicenseNo(String licenseNo);

    @Query("SELECT d.id AS id,d.name AS name FROM Doctor d")
    List<DoctorDropdown>findAllByDoctorDropdowns();
}
