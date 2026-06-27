package com.hms.profile.service;

import com.hms.profile.dto.AdminDTO;
import com.hms.profile.entity.Admin;
import com.hms.profile.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Long addAdmin(AdminDTO adminDTO) {
        // If an admin with this email already exists, return their existing profile id
        return adminRepository.findByEmail(adminDTO.getEmail())
                .map(existing -> existing.getId())
                .orElseGet(() -> adminRepository.save(adminDTO.toEntity()).getId());
    }

    @Override
    public AdminDTO getAdminById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ADMIN_NOT_FOUND"))
                .toDTO();
    }

    @Override
    public AdminDTO getAdminByUserId(Long userId) {
        return adminRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("ADMIN_NOT_FOUND"))
                .toDTO();
    }

    @Override
    public AdminDTO updateAdmin(AdminDTO adminDTO) {
        // No id means no profile exists yet — create it
        if (adminDTO.getId() == null) {
            // Check by userId to avoid duplicates
            return adminRepository.findByUserId(adminDTO.getUserId())
                    .map(existing -> {
                        existing.setProfilePictureId(adminDTO.getProfilePictureId());
                        existing.setPhone(adminDTO.getPhone());
                        existing.setAddress(adminDTO.getAddress());
                        return adminRepository.save(existing).toDTO();
                    })
                    .orElseGet(() -> adminRepository.save(adminDTO.toEntity()).toDTO());
        }
        Admin existing = adminRepository.findById(adminDTO.getId())
                .orElseThrow(() -> new RuntimeException("ADMIN_NOT_FOUND"));
        existing.setProfilePictureId(adminDTO.getProfilePictureId());
        existing.setPhone(adminDTO.getPhone());
        existing.setAddress(adminDTO.getAddress());
        return adminRepository.save(existing).toDTO();
    }
}
