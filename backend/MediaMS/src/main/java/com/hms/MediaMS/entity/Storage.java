package com.hms.MediaMS.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Storage backend for persisted media", allowableValues = {"DB", "S3", "GC", "AC"})
public enum Storage {
    DB,S3,GC,AC
}
