package com.hms.MediaMS.service;

import com.hms.MediaMS.dto.MediaFileDTO;
import com.hms.MediaMS.entity.MediaFile;
import com.hms.MediaMS.entity.Storage;
import com.hms.MediaMS.repository.MediaFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements  MediaService{

    private final MediaFileRepository mediaFileRepository;
    @Override
    public MediaFileDTO storeFile(MultipartFile file) throws IOException {
        MediaFile mediaFile = MediaFile.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .size(file.getSize())
                .data(file.getBytes())
                .storage(Storage.DB)
                .build();


        MediaFile savedFile = mediaFileRepository.save(mediaFile);
        return MediaFileDTO.builder()
                .id(savedFile.getId())
                .name(savedFile.getName())
                .type(savedFile.getType())
                .size(savedFile.getSize())
                .build();
    }
    @Override
    public Optional<MediaFile> getFile(Long id) {
        return mediaFileRepository.findById(id);
    }
}
