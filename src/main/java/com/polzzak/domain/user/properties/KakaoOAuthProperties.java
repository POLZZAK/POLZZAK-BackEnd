package com.polzzak.domain.user.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "oauth2.kakao")
public class KakaoOAuthProperties extends OAuthProperties {
	private final String apiKey;
	private final String secretKey;
	private final String kakaoUserInfoUrl;

	public KakaoOAuthProperties(String apiKey, String secretKey, String kakaoUserInfoUrl) {
		this.apiKey = apiKey == null ? "" : apiKey;
		this.secretKey = secretKey == null ? "" : secretKey;
		this.kakaoUserInfoUrl = kakaoUserInfoUrl == null ? "" : kakaoUserInfoUrl;
	}

	@Override
	public String getUserInfoUrl() {
		return this.kakaoUserInfoUrl;
	}
}
