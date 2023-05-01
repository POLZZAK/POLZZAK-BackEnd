package com.polzzak.application;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void uploadFile(final MultipartFile image, final String fileKey);

    String getSignedUrl(final String fileKey);

    void deleteFile(final String fileKey);
}
