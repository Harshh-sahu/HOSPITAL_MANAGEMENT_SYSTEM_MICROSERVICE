package com.hms.MediaMS.service;

import com.hms.MediaMS.dto.MediaFileDTO;
import com.hms.MediaMS.entity.MediaFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface MediaService {

    MediaFileDTO storeFile(MultipartFile file)throws IOException;

    public Optional<MediaFile>getFile(Long id);



}
