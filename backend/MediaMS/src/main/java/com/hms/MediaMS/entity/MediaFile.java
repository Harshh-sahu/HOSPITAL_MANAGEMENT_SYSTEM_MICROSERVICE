package com.hms.MediaMS.entity;


import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Schema(name = "MediaFile", description = "Persisted media file record")
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Media id", example = "101")
    private Long id;

    @Schema(description = "Original file name", example = "xray-report.pdf")
    private String name;
    @Schema(description = "Content type", example = "application/pdf")
    private String type;

    @Schema(description = "File size in bytes", example = "582341")
    private Long size;

    @Lob
    @Schema(description = "Raw file content")
    private byte[] data;


    @Schema(description = "Storage backend", implementation = Storage.class)
    private Storage storage;

    @CreationTimestamp
    @Schema(description = "Upload timestamp", example = "2026-03-30T18:25:00")
    private LocalDateTime createdAt;





}
