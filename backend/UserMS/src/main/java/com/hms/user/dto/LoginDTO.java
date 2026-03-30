package com.hms.user.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoginDTO", description = "User login credentials")
public class LoginDTO {

    @Schema(description = "Login email", example = "aman@hms.com")
    @NotBlank(message = "email cannot be blank")
    @Email(message = "email should be valid")
    private String email;

    @Schema(description = "Login password", example = "Pass@1234")
    @NotBlank(message = "password cannot be blank")
    private String  password;


}
