package com.polzzak.support;

import static com.polzzak.global.common.HeadersConstant.*;
import static com.polzzak.support.UserFixtures.*;

import com.polzzak.global.security.TokenPayload;

import jakarta.servlet.http.Cookie;

public class TokenFixtures {
	public static final String USER_ACCESS_TOKEN = "accessToken";
	public static final String ADMIN_ACCESS_TOKEN = "adminAccessToken";
	public static final String USER_REFRESH_TOKEN = "refreshToken";
	public static final String ADMIN_REFRESH_TOKEN = "adminRefreshToken";
	public static final String INVALID_REFRESH_TOKEN = "InvalidRefreshToken";
	public static final String INVALID_ACCESS_TOKEN = "inValidAccessToken";
	public static final String EXPIRED_ACCESS_TOKEN = "ExpiredAccessToken";
	public static final Cookie REFRESH_COOKIE = new Cookie(REFRESH_TOKEN_HEADER, USER_REFRESH_TOKEN);
	public static final Cookie INVALID_REFRESH_COOKIE = new Cookie(REFRESH_TOKEN_HEADER, INVALID_REFRESH_TOKEN);
	public static final String TOKEN_TYPE = "Bearer ";
	public static final TokenPayload USER_TOKEN_PAYLOAD = new TokenPayload(TEST_MEMBER_ID.toString(), TEST_USERNAME,
		TEST_USER_ROLE);
	public static final TokenPayload ADMIN_TOKEN_PAYLOAD = new TokenPayload(TEST_MEMBER_ID.toString(), TEST_USERNAME,
		TEST_ADMIN_ROLE);
}
