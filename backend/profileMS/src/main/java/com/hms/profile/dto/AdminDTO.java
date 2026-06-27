package com.hms.profile.dto;

import com.hms.profile.entity.Admin;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "AdminDTO", description = "Admin profile details")
public class AdminDTO {

    @Schema(description = "Admin profile id", example = "3001")
    private Long id;

    @Schema(description = "Linked UserMS user id", example = "1")
    private Long userId;

    @Schema(description = "Admin full name", example = "Super Admin")
    @NotBlank(message = "name is required")
    @Size(max = 120, message = "name must be at most 120 characters")
    private String name;

    @Schema(description = "Admin email", example = "admin@hms.com")
    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    @Schema(description = "Profile picture media id", example = "701")
    private Long profilePictureId;

    @Schema(description = "Phone number", example = "9876543210")
    @Size(max = 20, message = "phone must be at most 20 characters")
    private String phone;

    @Schema(description = "Address", example = "HQ, New Delhi")
    @Size(max = 255, message = "address must be at most 255 characters")
    private String address;

    public Admin toEntity() {
        return new Admin(this.id, this.userId, this.name, this.email,
                this.profilePictureId, this.phone, this.address);
    }
}
