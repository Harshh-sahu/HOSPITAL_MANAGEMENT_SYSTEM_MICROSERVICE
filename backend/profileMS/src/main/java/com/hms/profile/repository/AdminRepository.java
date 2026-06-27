package com.hms.profile.repository;

import com.hms.profile.entity.Admin;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AdminRepository extends CrudRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByUserId(Long userId);
}
