package com.hms.MediaMS.api;

import com.hms.MediaMS.dto.MediaFileDTO;
import com.hms.MediaMS.entity.MediaFile;
import com.hms.MediaMS.exception.ErrorInfo;
import com.hms.MediaMS.exception.MediaException;
import com.hms.MediaMS.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/media")
@Validated
@Tag(name = "Media APIs", description = "Operations for media upload and download")
@SecurityRequirement(name = "X-Secret-Key")
public class MediaAPI {


    private final MediaService mediaService;

    @Operation(
            operationId = "uploadFile",
            summary = "Upload media file",
            description = "Uploads a single file and stores it in the configured backend"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully",
                    content = @Content(schema = @Schema(implementation = MediaFileDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file upload request",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "File upload failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping("/upload")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Multipart form-data containing media file",
            content = @Content(mediaType = "multipart/form-data")
    )
    public ResponseEntity<MediaFileDTO> uploadFile(
            @Parameter(description = "File to upload", required = true) @RequestParam("file") MultipartFile file
    ){

        if (file.isEmpty()) {
            throw new MediaException("File must not be empty", HttpStatus.BAD_REQUEST);
        }
        MediaFileDTO mediaFileDTO = mediaService.storeFile(file);
        return ResponseEntity.ok(mediaFileDTO);
    }

    @Operation(
            operationId = "getFile",
            summary = "Download media file by id",
            description = "Returns raw file bytes with original content type and attachment header"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File found and returned",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "404", description = "File not found",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(
            @Parameter(description = "Media file id", example = "101") @PathVariable @Positive(message = "id must be positive") Long id
    ){
        Optional<MediaFile> mediaFileOptional = mediaService.getFile(id);
        if(mediaFileOptional.isPresent()){
            MediaFile mediaFile = mediaFileOptional.get();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + mediaFile.getName() + "\"")
                    .contentType(MediaType.parseMediaType(mediaFile.getType()))
                    .body(mediaFile.getData());
        }else{
            throw new MediaException("File not found", HttpStatus.NOT_FOUND);
        }
    }

}
