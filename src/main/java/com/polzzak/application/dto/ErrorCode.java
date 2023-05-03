package com.polzzak.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 430~439 Jwt Exception 처리
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    OK(HttpStatus.OK, 200, "OK"),
    CREATED(HttpStatus.CREATED, 201, "CREATED"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401, "인증되지 않은 요청입니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "접근 권한이 없습니다"),
    REQUEST_RESOURCE_NOT_VALID(HttpStatus.BAD_REQUEST, 410, "요청 자원이 유효하지 않습니다"),
    OAUTH_AUTHENTICATION_FAIL(HttpStatus.UNAUTHORIZED, 411, "OAUTH2 인증에 실패했습니다"),
    REQUIRED_REGISTER(HttpStatus.BAD_REQUEST, 412, "회원가입이 필요합니다"),
    FILE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 450, "파일 업로드에 실패했습니다"),
    FIND_FILE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 451, "파일 조회에 실패했습니다"),
    DELETE_FILE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 452, "파일 삭제에 실패했습니다");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
