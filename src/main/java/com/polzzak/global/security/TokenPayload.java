package com.polzzak.global.security;

public record TokenPayload(
	String username,
	String userRole
) {
}
