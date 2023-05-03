package com.polzzak.auth.model;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "oauth2.google")
public class GoogleOAuthProperties {
    private final String apiKey;
    private final String secretKey;
    private final String googleTokenUrl;
    private final String googleUserInfoUrl;

    public GoogleOAuthProperties(String apiKey, String secretKey, String googleTokenUrl, String googleUserInfoUrl) {
        this.apiKey = apiKey == null ? "" : apiKey;
        this.secretKey = secretKey == null ? "" : secretKey;
        this.googleTokenUrl = googleTokenUrl == null ? "" : googleTokenUrl;
        this.googleUserInfoUrl = googleUserInfoUrl == null ? "" : googleUserInfoUrl;
    }
}
