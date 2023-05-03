package com.polzzak.auth.model;

public class JwtException extends RuntimeException {
    public JwtException(final String message) {
        super(message);
    }
}
