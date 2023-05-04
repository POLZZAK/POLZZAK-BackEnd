package com.polzzak.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401, "인증되지 않은 요청입니다"),
	FORBIDDEN(HttpStatus.FORBIDDEN, 403, "접근 권한이 없습니다"),
	REQUEST_RESOURCE_NOT_VALID(HttpStatus.BAD_REQUEST, 410, "요청 자원이 유효하지 않습니다"),
	OAUTH_AUTHENTICATION_FAIL(HttpStatus.UNAUTHORIZED, 411, "OAUTH2 인증에 실패했습니다"),
	REQUIRED_REGISTER(HttpStatus.BAD_REQUEST, 412, "회원가입이 필요합니다"),
	ACCESS_TOKEN_INVALID(HttpStatus.BAD_REQUEST, 431, "ACCESS_TOKEN이 유효하지 않습니다"),
	REFRESH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, 432, "REFRESH_TOKEN이 유효하지 않습니다"),
	ACCESS_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, 433, "ACCESS_TOKEN이 만료되었습니다"),
	TOKEN_REISSUE_SUCCESS(HttpStatus.BAD_REQUEST, 434, "토큰 재발급에 성공했습니다"),
	FILE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 450, "파일 업로드에 실패했습니다"),
	FIND_FILE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 451, "파일 조회에 실패했습니다"),
	DELETE_FILE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 452, "파일 삭제에 실패했습니다");

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}
