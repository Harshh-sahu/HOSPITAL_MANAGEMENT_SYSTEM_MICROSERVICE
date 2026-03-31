package com.hms.profile.dto;

import com.hms.profile.entity.Patient;
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
@Schema(name = "PatientDTO", description = "Patient profile details")
public class PatientDTO {
    @Schema(description = "Patient id", example = "2001")
    private Long id;
    @Schema(description = "Patient full name", example = "Aman Sharma")
    @NotBlank(message = "name is required")
    @Size(max = 120, message = "name must be at most 120 characters")
    private String name;
    @Schema(description = "Patient email", example = "aman.sharma@hms.com")
    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;
    @Schema(description = "Date of birth", example = "1997-11-02")
    private LocalDate dob;
    @Schema(description = "Profile picture media id", example = "601")
    private Long profilePictureId;

    @Schema(description = "Phone number", example = "9988776655")
    @Size(max = 20, message = "phone must be at most 20 characters")
    private String phone;
    @Schema(description = "Address", example = "Lajpat Nagar, Delhi")
    @Size(max = 255, message = "address must be at most 255 characters")
    private String address;
    @Schema(description = "Aadhar number", example = "123456789012")
    @Size(max = 20, message = "aadharNo must be at most 20 characters")
    private String aadharNo;
    @Schema(description = "Blood group", implementation = BloodGroup.class)
    private BloodGroup bloodGroup;
    @Schema(description = "Known allergies", example = "Pollen")
    @Size(max = 255, message = "allergies must be at most 255 characters")
    private String allergies;
    @Schema(description = "Known chronic diseases", example = "Diabetes")
    @Size(max = 255, message = "chronicDisease must be at most 255 characters")
    private String chronicDisease;
    public Patient toEntity(){
         return new Patient(this.id,this.name,this.email,this.dob,this.profilePictureId,this.phone,this.address,this.aadharNo,this.bloodGroup,this.allergies,this.chronicDisease);
    }

}
