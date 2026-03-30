package com.hms.user.api;

import com.hms.user.dto.LoginDTO;
import com.hms.user.dto.RegistrationCountDTO;
import com.hms.user.dto.ResponseDTO;
import com.hms.user.dto.UserDTO;
import com.hms.user.exception.HmsException;
import com.hms.user.jwt.JwtUtil;
import com.hms.user.service.UserService;
import com.hms.user.utility.ErrorInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
@Validated
@CrossOrigin
@RequiredArgsConstructor
@Tag(name = "User APIs", description = "User registration, authentication, and registration analytics")
@SecurityRequirement(name = "X-Secret-Key")
public class UserAPI {

    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Operation(operationId = "registerUser", summary = "Register user", description = "Registers a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping("/register")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "User registration payload",
            content = @Content(schema = @Schema(implementation = UserDTO.class)))
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody @Valid UserDTO userDTO)throws HmsException {
        userService.registerUser(userDTO);

        return new ResponseEntity<>(new ResponseDTO("Account created."),HttpStatus.CREATED);
    }

    @Operation(operationId = "loginUser", summary = "Login user", description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiJ9..."))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Invalid credentials or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping("/login")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Login payload",
            content = @Content(schema = @Schema(implementation = LoginDTO.class)))
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginDTO loginDTO) throws HmsException {
try{
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    loginDTO.getEmail(),
                    loginDTO.getPassword()
            )
    );

}catch (AuthenticationException e){
    throw  new HmsException("INVALID_CREDENTIALS");
}

final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());
final String jwt = jwtUtil.generateToken(userDetails);

return new ResponseEntity<>(jwt,HttpStatus.OK);
    }


    @Operation(operationId = "test", summary = "Health test endpoint", description = "Returns static test string")
    @ApiResponse(responseCode = "200", description = "Test response",
            content = @Content(schema = @Schema(type = "string", example = "Test")))
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return new ResponseEntity<>("Test",HttpStatus.OK);
}



    @Operation(operationId = "getProfile", summary = "Get linked profile id", description = "Returns profile id associated with a user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile id fetched",
                    content = @Content(schema = @Schema(type = "integer", format = "int64", example = "1001"))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/getProfile/{id}")
    public ResponseEntity<Long> getProfile(
            @Parameter(description = "User id", example = "1") @PathVariable Long id
    ) throws HmsException{
        return new ResponseEntity<>(userService.getProfile(id),HttpStatus.OK);
}

    @Operation(operationId = "getMonthlyRegistrationCounts", summary = "Get monthly registration counts", description = "Returns doctor and patient monthly registration trend")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration counts fetched",
                    content = @Content(schema = @Schema(implementation = RegistrationCountDTO.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/getRegistrationCounts")
    public ResponseEntity<RegistrationCountDTO> getMonthlyRegistrationCounts() throws HmsException{
        return new ResponseEntity<>(userService.getMonthlyRegistrationCounts(),HttpStatus.OK);

}
}
