package com.polzzak.global.security;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode {
	ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, 431, "ACCESS_TOKEN이 유효하지 않습니다"),
	REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, 432, "REFRESH_TOKEN이 유효하지 않습니다"),
	ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 433, "ACCESS_TOKEN이 만료되었습니다"),
	TOKEN_REISSUE_SUCCESS(HttpStatus.BAD_REQUEST, 434, "토큰 재발급에 성공했습니다"),
	TOKEN_UNAUTHORIZED(HttpStatus.FORBIDDEN, 435, "요청 권한이 없습니다");

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}
