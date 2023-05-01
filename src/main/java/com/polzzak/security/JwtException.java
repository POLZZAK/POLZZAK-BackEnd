package com.polzzak.security;

public class JwtException extends RuntimeException {
    public JwtException(final String message) {
        super(message);
    }
}
