package com.polzzak.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.polzzak.auth.model.AmazonS3Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {

    private final AmazonS3Properties amazonS3Properties;

    public AmazonS3Config(AmazonS3Properties amazonS3Properties) {
        this.amazonS3Properties = amazonS3Properties;
    }

    @Bean
    public AmazonS3 s3Client(final AWSCredentials awsCredentials, final ClientConfiguration clientConfiguration) {
        return AmazonS3Client.builder()
            .withRegion(Regions.AP_NORTHEAST_2)
            .withForceGlobalBucketAccessEnabled(true)
            .withClientConfiguration(clientConfiguration)
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .build();
    }

    @Bean
    public AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(amazonS3Properties.getAccessKey(), amazonS3Properties.getSecretKey());
    }

    @Bean
    public ClientConfiguration clientConfiguration() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration
            .withTcpKeepAlive(true)
            .withMaxConnections(100)
            .withProtocol(Protocol.HTTP)
            .withMaxErrorRetry(15);

        return clientConfiguration;
    }
}
