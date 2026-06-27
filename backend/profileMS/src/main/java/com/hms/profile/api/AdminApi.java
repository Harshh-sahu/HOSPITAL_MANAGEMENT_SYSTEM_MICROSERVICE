package com.hms.profile.api;

import com.hms.profile.dto.AdminDTO;
import com.hms.profile.service.AdminService;
import com.hms.profile.utility.ErrorInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("profile/admin")
@Validated
@Tag(name = "Profile Admin APIs", description = "Admin profile operations")
@SecurityRequirement(name = "X-Secret-Key")
@RequiredArgsConstructor
public class AdminApi {

    private final AdminService adminService;

    @Operation(operationId = "addAdmin", summary = "Add admin", description = "Creates admin profile and returns id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin created",
                    content = @Content(schema = @Schema(type = "integer", format = "int64", example = "3001"))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping("/add")
    public ResponseEntity<Long> addAdmin(@RequestBody AdminDTO adminDTO) {
        // UserMS sends UserDTO; the 'id' field carries the UserMS user id.
        // Map it to userId and let the DB generate the profile id.
        adminDTO.setUserId(adminDTO.getId());
        adminDTO.setId(null);
        return new ResponseEntity<>(adminService.addAdmin(adminDTO), HttpStatus.CREATED);
    }

    @Operation(operationId = "getAdminById", summary = "Get admin by profile id", description = "Returns admin profile details by profile id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin fetched",
                    content = @Content(schema = @Schema(implementation = AdminDTO.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<AdminDTO> getAdminById(
            @Parameter(description = "Admin profile id", example = "3001") @PathVariable Long id
    ) {
        return new ResponseEntity<>(adminService.getAdminById(id), HttpStatus.OK);
    }

    @Operation(operationId = "getAdminByUserId", summary = "Get admin by user id", description = "Returns admin profile by UserMS user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin fetched",
                    content = @Content(schema = @Schema(implementation = AdminDTO.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/getByUserId/{userId}")
    public ResponseEntity<AdminDTO> getAdminByUserId(
            @Parameter(description = "UserMS user id", example = "1") @PathVariable Long userId
    ) {
        return new ResponseEntity<>(adminService.getAdminByUserId(userId), HttpStatus.OK);
    }

    @Operation(operationId = "updateAdmin", summary = "Update admin", description = "Updates an existing admin profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin updated",
                    content = @Content(schema = @Schema(implementation = AdminDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PutMapping("/update")
    public ResponseEntity<AdminDTO> updateAdmin(@RequestBody AdminDTO adminDTO) {
        return new ResponseEntity<>(adminService.updateAdmin(adminDTO), HttpStatus.OK);
    }

    @Operation(operationId = "getAdminProfileId", summary = "Get admin profile picture id", description = "Returns media id of admin profile picture")
    @ApiResponse(responseCode = "200", description = "Profile picture id fetched",
            content = @Content(schema = @Schema(type = "integer", format = "int64", example = "701")))
    @GetMapping("/getProfileId/{id}")
    public ResponseEntity<Long> getProfileId(
            @Parameter(description = "Admin profile id", example = "3001") @PathVariable Long id
    ) {
        return new ResponseEntity<>(adminService.getAdminById(id).getProfilePictureId(), HttpStatus.OK);
    }
}
