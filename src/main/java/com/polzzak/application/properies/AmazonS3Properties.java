package com.polzzak.application.properies;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "s3")
public class AmazonS3Properties {
    private final String accessKey;
    private final String secretKey;
    private final int urlValidTime;
    private final String bucketName;

    public AmazonS3Properties(final String accessKey, final String secretKey, final int urlValidTime, final String bucketName) {
        this.accessKey = accessKey == null ? "" : accessKey;
        this.secretKey = secretKey == null ? "" : secretKey;
        this.urlValidTime = urlValidTime;
        this.bucketName = bucketName == null ? "" : bucketName;
    }
}
