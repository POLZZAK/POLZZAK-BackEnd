package com.polzzak.global.exception;

import static com.polzzak.global.common.HeadersConstant.*;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.security.TokenProvider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	private final TokenProvider tokenProvider;

	public CustomExceptionHandler(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Override
	protected ResponseEntity handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
		final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
		log.error("[MethodArgumentNotValidException] ", ex);
		List<String> errorMessages = ex.getBindingResult().getFieldErrors()
			.stream()
			.map(it -> it.getField() + " " + it.getDefaultMessage())
			.collect(Collectors.toList());

		return ResponseEntity
			.status(ErrorCode.REQUEST_RESOURCE_NOT_VALID.getHttpStatus())
			.body(ApiResponse.error(ErrorCode.REQUEST_RESOURCE_NOT_VALID.getCode(), errorMessages));
	}

	@ExceptionHandler(PolzzakException.class)
	public ResponseEntity<ApiResponse> polzzakException(final PolzzakException ex) {
		log.error("[PolzzakException] ", ex);

		return ResponseEntity
			.status(ex.getErrorCode().getHttpStatus())
			.body(ApiResponse.error(ex.getErrorCode().getCode(), ex.getErrorCode().getMessage()));
	}

	@ExceptionHandler(JwtException.class)
	public ResponseEntity handleJwtException(
		final JwtException ex,
		final HttpServletRequest httpServletRequest,
		final HttpServletResponse httpServletResponse
	) {
		log.error("[JwtException] {}", ex.getMessage());

		if (ex.getErrorCode() != ErrorCode.ACCESS_TOKEN_EXPIRED) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(ex.getErrorCode()));
		}

		Cookie cookie = WebUtils.getCookie(httpServletRequest, REFRESH_TOKEN_HEADER);
		if (!isValidRefreshToken(cookie)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(ErrorCode.REFRESH_TOKEN_INVALID));
		}

		String refreshToken = cookie.getValue();
		String payload = tokenProvider.getSubject(refreshToken);
		addRefreshCookie(httpServletResponse, payload);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.error(ErrorCode.TOKEN_REISSUE_SUCCESS, tokenProvider.createAccessToken(payload)));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse> illegalArgumentException(final IllegalArgumentException ex) {
		log.error("[IllegalArgumentException] ", ex);

		return ResponseEntity
			.badRequest()
			.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
	}

	@ExceptionHandler(InvalidContentTypeException.class)
	public ResponseEntity<ApiResponse> invalidContentTypeException(final InvalidContentTypeException ex) {
		log.error("[InvalidContentTypeException] ", ex);

		return ResponseEntity
			.badRequest()
			.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse> runtimeException(final RuntimeException ex) {
		log.error("[RuntimeException] ", ex);

		return ResponseEntity
			.internalServerError()
			.body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
	}

	private void addRefreshCookie(final HttpServletResponse httpServletResponse, final String payload) {
		Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_HEADER, tokenProvider.createRefreshToken(payload));
		refreshTokenCookie.setHttpOnly(true);
		httpServletResponse.addCookie(refreshTokenCookie);
	}

	private boolean isValidRefreshToken(final Cookie cookie) {
		if (cookie == null || !tokenProvider.isValidToken(cookie.getValue())) {
			return false;
		}

		return true;
	}
}
