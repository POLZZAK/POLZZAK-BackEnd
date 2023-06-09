package com.polzzak.domain.user.properties;

public abstract class OAuthProperties {
	private String apiKey;
	private String secretKey;
	private String userInfoUrl;

	public abstract String getApiKey();

	public abstract String getSecretKey();

	public abstract String getUserInfoUrl();
}
