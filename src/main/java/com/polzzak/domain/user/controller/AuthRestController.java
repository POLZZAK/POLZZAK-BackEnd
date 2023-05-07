package com.polzzak.domain.user.controller;

import static com.polzzak.global.common.HeadersConstant.*;

import java.util.Locale;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.polzzak.domain.user.dto.AccessTokenResponse;
import com.polzzak.domain.user.dto.LoginRequest;
import com.polzzak.domain.user.dto.LoginResponse;
import com.polzzak.domain.user.dto.RegisterRequest;
import com.polzzak.domain.user.entity.SocialType;
import com.polzzak.domain.user.service.AuthenticationService;
import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.exception.ErrorCode;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {
	private final AuthenticationService authenticationService;

	public AuthRestController(final AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@PostMapping("/login/{social}")
	public ResponseEntity<ApiResponse> socialLogin(
		final @PathVariable("social") String social,
		final @RequestBody @Valid LoginRequest loginRequest,
		final HttpServletResponse httpServletResponse
	) {
		String username = authenticationService.getSocialUsername(loginRequest, social);

		try {
			authenticationService.validateUserByUsername(username);

			Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_HEADER,
				authenticationService.generateRefreshTokenByUsername(username));
			refreshTokenCookie.setHttpOnly(true);
			httpServletResponse.addCookie(refreshTokenCookie);

			AccessTokenResponse accessTokenResponse = new AccessTokenResponse(
				authenticationService.generateAccessTokenByUsername(username));

			return ResponseEntity.ok(ApiResponse.ok(accessTokenResponse));
		} catch (IllegalArgumentException e) {
			LoginResponse loginResponse = new LoginResponse(username,
				SocialType.valueOf(social.toUpperCase(Locale.ROOT)));
			return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.REQUIRED_REGISTER, loginResponse));
		}
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse> register(
		final @RequestPart @Valid RegisterRequest registerRequest,
		final @RequestPart(required = false) MultipartFile profile,
		final HttpServletResponse httpServletResponse
	) {
		String username = authenticationService.register(registerRequest, profile);

		Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_HEADER,
			authenticationService.generateRefreshTokenByUsername(username));
		refreshTokenCookie.setHttpOnly(true);
		httpServletResponse.addCookie(refreshTokenCookie);

		AccessTokenResponse accessTokenResponse = new AccessTokenResponse(
			authenticationService.generateAccessTokenByUsername(username));
		return ResponseEntity.ok(ApiResponse.ok(accessTokenResponse));
	}

	@GetMapping("/validate/nickname")
	public ResponseEntity<Void> validateNickname(final @RequestParam("value") String nickname) {
		authenticationService.validateNickname(nickname);
		return ResponseEntity.noContent().build();
	}
}
