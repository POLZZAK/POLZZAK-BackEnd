package com.polzzak.global.auth.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
	private final String key;
	private final String type;
	private final long expiredTimeMs;
	private final long refreshExpiredTimeMs;

	public JwtProperties(final String key, final String type, final long expiredTimeMs,
		final long refreshExpiredTimeMs) {
		this.key = key == null ? "deafultKeydeafultKeydeafultKeydeafultKey" : key;
		this.type = type == null ? "" : type;
		this.expiredTimeMs = expiredTimeMs;
		this.refreshExpiredTimeMs = refreshExpiredTimeMs;
	}
}
