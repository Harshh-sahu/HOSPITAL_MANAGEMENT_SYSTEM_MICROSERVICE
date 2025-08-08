package com.hms.profile.repository;

import com.hms.profile.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.print.Doc;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {


    Optional<Doctor>    findByEmail(String email);

    Optional<Doctor> findByLicenseNo(String licenseNo);
}
