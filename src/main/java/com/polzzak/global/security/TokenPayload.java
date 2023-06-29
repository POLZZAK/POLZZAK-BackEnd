package com.polzzak.global.security;

public record TokenPayload(
	String id,
	String username,
	String userRole
) {
}
