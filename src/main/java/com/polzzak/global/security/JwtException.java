package com.polzzak.global.security;

import lombok.Getter;

@Getter
public class JwtException extends RuntimeException {
	private JwtErrorCode jwtErrorCode;

	public JwtException(final JwtErrorCode jwtErrorCode) {
		this.jwtErrorCode = jwtErrorCode;
	}

	@Override
	public String getMessage() {
		return this.jwtErrorCode.getMessage();
	}
}
