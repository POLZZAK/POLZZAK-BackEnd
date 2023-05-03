package com.polzzak.auth.model;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "oauth2.kakao")
public class KakaoOAuthProperties {
    private final String apiKey;
    private final String secretKey;
    private final String kakaoTokenUrl;
    private final String kakaoUserInfoUrl;

    public KakaoOAuthProperties(String apiKey, String secretKey, String kakaoTokenUrl, String kakaoUserInfoUrl) {
        this.apiKey = apiKey == null ? "" : apiKey;
        this.secretKey = secretKey == null ? "" : secretKey;
        this.kakaoTokenUrl = kakaoTokenUrl == null ? "" : kakaoTokenUrl;
        this.kakaoUserInfoUrl = kakaoUserInfoUrl == null ? "" : kakaoUserInfoUrl;
    }
}
