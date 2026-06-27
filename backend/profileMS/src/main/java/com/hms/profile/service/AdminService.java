package com.hms.profile.service;

import com.hms.profile.dto.AdminDTO;

public interface AdminService {
    Long addAdmin(AdminDTO adminDTO);
    AdminDTO getAdminById(Long id);
    AdminDTO getAdminByUserId(Long userId);
    AdminDTO updateAdmin(AdminDTO adminDTO);
}
