package com.polzzak.security;

public class JwtExpiredException extends JwtException {
    public JwtExpiredException(final String message) {
        super(message);
    }
}
