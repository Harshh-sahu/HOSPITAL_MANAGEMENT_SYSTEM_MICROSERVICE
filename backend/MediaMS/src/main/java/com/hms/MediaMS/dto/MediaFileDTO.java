package com.hms.MediaMS.dto;


import com.hms.MediaMS.entity.Storage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "MediaFileDTO", description = "Uploaded media metadata")
public class MediaFileDTO {
    @Schema(description = "Media id", example = "101")
    private Long id;
    @Schema(description = "Original file name", example = "xray-report.pdf")
    private String name;
    @Schema(description = "Content type", example = "application/pdf")
    private String type;
    @Schema(description = "File size in bytes", example = "582341")
    private Long size;
    @Schema(description = "Storage backend", implementation = Storage.class, example = "DB")
    private Storage storage;

}
