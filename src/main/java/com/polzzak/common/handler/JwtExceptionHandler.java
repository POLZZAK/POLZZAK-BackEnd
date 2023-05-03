package com.polzzak.common.handler;

import com.polzzak.auth.model.JwtException;
import com.polzzak.auth.model.JwtExpiredException;
import com.polzzak.auth.model.JwtTokenProvider;
import com.polzzak.auth.model.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.WebUtils;

import static com.polzzak.auth.model.Headers.REFRESH_TOKEN_HEADER;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtExceptionHandler {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtExceptionHandler(final JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @ExceptionHandler(JwtExpiredException.class)
    public ResponseEntity handleJwtExpiredException(
        final JwtException ex,
        final HttpServletRequest httpServletRequest,
        final HttpServletResponse httpServletResponse
    ) {
        log.error("[JwtExpiredException] {}", ex.getMessage());

        Cookie cookie = WebUtils.getCookie(httpServletRequest, REFRESH_TOKEN_HEADER);
        if (!isValidRefreshToken(cookie)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TokenResponse.refreshTokenNotValid());
        }

        String refreshToken = cookie.getValue();
        String payload = jwtTokenProvider.getSubject(refreshToken);
        addRefreshCookie(httpServletResponse, payload);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(TokenResponse.success(jwtTokenProvider.createAccessToken(payload)));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity handleJwtException(
        final JwtException ex
    ) {
        log.error("[JwtException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TokenResponse.tokenNotValid());
    }

    private void addRefreshCookie(final HttpServletResponse httpServletResponse, final String payload) {
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_HEADER, jwtTokenProvider.createRefreshToken(payload));
        refreshTokenCookie.setHttpOnly(true);
        httpServletResponse.addCookie(refreshTokenCookie);
    }

    private boolean isValidRefreshToken(final Cookie cookie) {
        if (cookie == null || !jwtTokenProvider.isValidToken(cookie.getValue())) {
            return false;
        }

        return true;
    }
}
