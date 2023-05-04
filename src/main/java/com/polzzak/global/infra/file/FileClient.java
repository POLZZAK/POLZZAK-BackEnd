package com.polzzak.global.infra.file;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.polzzak.global.common.FileType;
import com.polzzak.global.infra.file.properies.FolderProperties;

@Service
public class FileClient {
	private final StorageClient storageClient;

	private final FolderProperties folderProperties;

	public FileClient(final StorageClient storageClient, final FolderProperties folderProperties) {
		this.storageClient = storageClient;
		this.folderProperties = folderProperties;
	}

	public String uploadFile(final MultipartFile file, final FileType fileType) {
		String serverFileName = createServerFileName(file.getOriginalFilename());
		String fileKey = getFileKey(serverFileName, fileType);

		storageClient.uploadFile(file, fileKey);

		return fileKey;
	}

	public String getSignedUrl(final String fileKey) {
		return storageClient.getSignedUrl(fileKey);
	}

	public void deleteFile(final String fileKey) {
		storageClient.deleteFile(fileKey);
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
