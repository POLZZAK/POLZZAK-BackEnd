package com.polzzak.domain.user.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "oauth2.google")
public class GoogleOAuthProperties extends OAuthProperties {
	private final String apiKey;
	private final String secretKey;
	private final String googleUserInfoUrl;

	public GoogleOAuthProperties(String apiKey, String secretKey, String googleUserInfoUrl) {
		this.apiKey = apiKey == null ? "" : apiKey;
		this.secretKey = secretKey == null ? "" : secretKey;
		this.googleUserInfoUrl = googleUserInfoUrl == null ? "" : googleUserInfoUrl;
	}

	@Override
	public String getUserInfoUrl() {
		return this.googleUserInfoUrl;
	}
}
