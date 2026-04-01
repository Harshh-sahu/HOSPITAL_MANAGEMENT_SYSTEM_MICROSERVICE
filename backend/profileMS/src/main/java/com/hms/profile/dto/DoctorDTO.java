package com.hms.profile.dto;

import com.hms.profile.entity.Doctor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "DoctorDTO", description = "Doctor profile details")
public class DoctorDTO {
    @Schema(description = "Doctor id", example = "1001")
    private Long id;
    @Schema(description = "Doctor full name", example = "Dr. Priya Mehta")
    @NotBlank(message = "name is required")
    @Size(max = 120, message = "name must be at most 120 characters")
    private String name;
    @Schema(description = "Doctor email", example = "priya.mehta@hms.com")
    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;
    @Schema(description = "Date of birth", example = "1988-04-12")
    private LocalDate dob;
    @Schema(description = "Profile picture media id", example = "501")
    private Long profilePictureId;

    @Schema(description = "Phone number", example = "9876543210")
    @Size(max = 20, message = "phone must be at most 20 characters")
    private String phone;
    @Schema(description = "Address", example = "Sector 21, Noida")
    @Size(max = 255, message = "address must be at most 255 characters")
    private String address;
    @Schema(description = "Medical license number", example = "LIC-29384")
    @Size(max = 60, message = "licenseNo must be at most 60 characters")
    private String licenseNo;
    @Schema(description = "Specialization", example = "Cardiology")
    @Size(max = 80, message = "specialization must be at most 80 characters")
    private String specialization;
    @Schema(description = "Department", example = "Internal Medicine")
    @Size(max = 80, message = "department must be at most 80 characters")
    private String department;
    @Schema(description = "Total years of experience", example = "8")
    private Integer totalExp;

    public Doctor toEntity(){
        return new Doctor(this.id, this.name, this.email, this.dob,this.profilePictureId, this.phone, this.address, this.licenseNo, this.specialization, this.department, this.totalExp);
       }
}
