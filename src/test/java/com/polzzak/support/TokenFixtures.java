package com.polzzak.support;

import static com.polzzak.global.common.HeadersConstant.*;

import jakarta.servlet.http.Cookie;

public class TokenFixtures {
	public static final String ACCESS_TOKEN = "accessToken";
	public static final String REFRESH_TOKEN = "RefreshToken";
	public static final String INVALID_REFRESH_TOKEN = "InvalidRefreshToken";
	public static final String INVALID_ACCESS_TOKEN = "inValidAccessToken";
	public static final String EXPIRED_ACCESS_TOKEN = "ExpiredAccessToken";
	public static final Cookie REFRESH_COOKIE = new Cookie(REFRESH_TOKEN_HEADER, "RefreshToken");
	public static final Cookie INVALID_REFRESH_COOKIE = new Cookie(REFRESH_TOKEN_HEADER, "InvalidRefreshToken");
	public static final String TOKEN_TYPE = "Bearer ";
}
