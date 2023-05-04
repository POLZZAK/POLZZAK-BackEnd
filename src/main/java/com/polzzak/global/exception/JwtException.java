package com.polzzak.global.exception;

public class JwtException extends PolzzakException {
	public JwtException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
