package com.polzzak.auth.model;

public class JwtExpiredException extends JwtException {
    public JwtExpiredException(final String message) {
        super(message);
    }
}
