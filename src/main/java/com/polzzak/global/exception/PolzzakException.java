package com.polzzak.global.exception;

import lombok.Getter;

@Getter
public class PolzzakException extends RuntimeException {
	private ErrorCode errorCode;

	public PolzzakException(final ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public String getMessage() {
		return this.errorCode.getMessage();
	}
}
