package com.polzzak.security;

public record TokenResponse(
    int code,
    String message,
    String token
) {
    public static TokenResponse tokenNotValid() {
        return new TokenResponse(431, "AccessToken is invalid", null);
    }

    public static TokenResponse refreshTokenNotValid() {
        return new TokenResponse(432, "RefreshToken is invalid", null);
    }

    public static TokenResponse success(final String token) {
        return new TokenResponse(433, "token reissue success", token);
    }
}
