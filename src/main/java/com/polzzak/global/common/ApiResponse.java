package com.polzzak.global.common;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.polzzak.global.exception.ErrorCode;

public record ApiResponse<T>(
	int code,
	List<String> messages,
	T data
) {
	public static ApiResponse error(final int code, final String message) {
		return new ApiResponse(code, List.of(message), null);
	}

	public static ApiResponse error(final int code, final List<String> messages) {
		return new ApiResponse(code, messages, null);
	}

	public static ApiResponse error(final ErrorCode errorCode) {
		return new ApiResponse(errorCode.getCode(), List.of(errorCode.getMessage()), null);
	}

	public static <T> ApiResponse<T> error(final ErrorCode errorCode, final T data) {
		return new ApiResponse(errorCode.getCode(), List.of(errorCode.getMessage()), data);
	}

	public static <T> ApiResponse<T> ok() {
		return new ApiResponse(HttpStatus.OK.value(), null, null);
	}

	public static <T> ApiResponse<T> ok(final T data) {
		return new ApiResponse(HttpStatus.OK.value(), null, data);
	}

	public static ApiResponse<Void> created() {
		return new ApiResponse(HttpStatus.CREATED.value(), null, null);
	}
}
