package com.hms.user.dto;

import com.hms.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserDTO", description = "User account payload")
public class UserDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "User id", example = "1")
    private Long id;

    @Schema(description = "Full name", example = "Aman Sharma")
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 120, message = "name must be at most 120 characters")
    private String name;

    @Schema(description = "Login email", example = "aman@hms.com")
    @Column(unique = true)
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "email Should be Valid")
    private String email;

    @Schema(description = "Password meeting complexity policy", example = "Pass@1234")
    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase, one lowercase, one digit, and one special character"
    )
    private String password;

    @Schema(description = "Assigned user role", implementation = Roles.class)
    @NotNull(message = "role cannot be null")
    private Roles role;

    @Schema(description = "Linked profile id from profileMS", example = "1001")
    private Long profileId;

    @Schema(description = "Creation timestamp", example = "2026-03-31T09:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2026-03-31T10:00:00")
    private LocalDateTime updatedAt;



    public User toEntity(){
        return new User(
            this.id,
            this.name,
            this.email,
            this.password,
            this.role,
                this.profileId,
                this.createdAt,
                this.updatedAt
        );
    }

}
