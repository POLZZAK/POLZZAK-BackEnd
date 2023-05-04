package com.polzzak.global.infra.file;

import org.springframework.web.multipart.MultipartFile;

public interface StorageClient {
	void uploadFile(final MultipartFile image, final String fileKey);

	String getSignedUrl(final String fileKey);

	void deleteFile(final String fileKey);
}
