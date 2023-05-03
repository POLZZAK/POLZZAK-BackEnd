package com.polzzak.file;

import com.polzzak.file.model.FolderProperties;
import com.polzzak.file.model.FileType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class FileService {
    private final StorageService storageService;

    private final FolderProperties folderProperties;

    public FileService(final StorageService storageService, final FolderProperties folderProperties) {
        this.storageService = storageService;
        this.folderProperties = folderProperties;
    }

    public String uploadFile(final MultipartFile file, final FileType fileType) {
        String serverFileName = createServerFileName(file.getOriginalFilename());
        String fileKey = getFileKey(serverFileName, fileType);

        storageService.uploadFile(file, fileKey);

        return fileKey;
    }

    public String getSignedUrl(final String fileKey) {
        return storageService.getSignedUrl(fileKey);
    }

    public void deleteFile(final String fileKey) {
        storageService.deleteFile(fileKey);
    }

    private String createServerFileName(final String originalFileName) {
        return UUID.randomUUID() + "." + getExtension(originalFileName);
    }

    private String getExtension(final String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
    }

    private String getFileKey(final String serverFileName, final FileType fileType) {
        if (fileType == fileType.PROFILE_IMAGE) {
            return folderProperties.getProfileFolderName() + serverFileName;
        }

        return folderProperties.getEtcFolderName() + serverFileName;
    }
}
