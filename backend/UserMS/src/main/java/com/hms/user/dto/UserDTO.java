package com.hms.user.dto;

import com.hms.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @Column(unique = true)
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "email Should be Valid")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase, one lowercase, one digit, and one special character"
    )
    private String password;
    private Roles role;
    private Long profileId;



    public User toEntity(){
        return new User(
            this.id,
            this.name,
            this.email,
            this.password,
            this.role,
                this.profileId
        );
    }

}
