package com.polzzak.global.infra.file;

import java.io.IOException;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.polzzak.global.exception.ErrorCode;
import com.polzzak.global.exception.PolzzakException;
import com.polzzak.global.infra.file.properies.AmazonS3Properties;

@Service
public class AmazonS3Client implements StorageClient {
	private final AmazonS3 s3Client;
	private final AmazonS3Properties amazonS3Properties;

	public AmazonS3Client(final AmazonS3 s3Client, final AmazonS3Properties amazonS3Properties) {
		this.s3Client = s3Client;
		this.amazonS3Properties = amazonS3Properties;
	}

	@Override
	public void uploadFile(final MultipartFile file, final String fileKey) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(file.getSize());
		objectMetadata.setContentType(file.getContentType());

		try {
			s3Client.putObject(amazonS3Properties.getBucketName(), fileKey, file.getInputStream(), objectMetadata);
		} catch (IOException | SdkClientException e) {
			throw new PolzzakException(ErrorCode.FILE_UPLOAD_FAIL);
		}
	}

	@Override
	public String getSignedUrl(final String fileKey) {
		GeneratePresignedUrlRequest presignedUrlRequest =
			new GeneratePresignedUrlRequest(amazonS3Properties.getBucketName(), fileKey);
		presignedUrlRequest.withMethod(HttpMethod.GET);
		presignedUrlRequest.withExpiration(new Date(System.currentTimeMillis() + amazonS3Properties.getUrlValidTime()));

		try {
			return s3Client.generatePresignedUrl(presignedUrlRequest).toString();
		} catch (SdkClientException e) {
			throw new PolzzakException(ErrorCode.FIND_FILE_FAIL);
		}
	}

	@Override
	public void deleteFile(final String fileKey) {
		try {
			s3Client.deleteObject(amazonS3Properties.getBucketName(), fileKey);
		} catch (SdkClientException e) {
			throw new PolzzakException(ErrorCode.DELETE_FILE_FAIL);
		}
	}
}
