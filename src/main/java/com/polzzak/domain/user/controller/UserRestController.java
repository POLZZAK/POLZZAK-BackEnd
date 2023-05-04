package com.polzzak.domain.user.controller;

import static com.polzzak.global.common.HeadersConstant.*;

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
import com.polzzak.domain.user.dto.MemberResponse;
import com.polzzak.domain.user.dto.RegisterRequest;
import com.polzzak.domain.user.entity.SocialType;
import com.polzzak.domain.user.service.UserAuthenticationService;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.global.auth.LoginUsername;
import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.exception.ErrorCode;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

	private final UserService userService;
	private final UserAuthenticationService userAuthenticationService;

	public UserRestController(final UserService userService,
		final UserAuthenticationService userAuthenticationService) {
		this.userService = userService;
		this.userAuthenticationService = userAuthenticationService;
	}

	@PostMapping("/login/{social}")
	public ResponseEntity<ApiResponse> socialLogin(
		final @PathVariable("social") String social,
		final @RequestBody @Valid LoginRequest loginRequest,
		final HttpServletResponse httpServletResponse
	) {
		String username = userAuthenticationService.getSocialUsername(loginRequest, social);

		try {
			userAuthenticationService.validateUserByUsername(username);

			Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_HEADER,
				userAuthenticationService.generateRefreshTokenByUsername(username));
			refreshTokenCookie.setHttpOnly(true);
			httpServletResponse.addCookie(refreshTokenCookie);

			AccessTokenResponse accessTokenResponse = new AccessTokenResponse(
				userAuthenticationService.generateAccessTokenByUsername(username));
			return ResponseEntity.ok(ApiResponse.ok(accessTokenResponse));
		} catch (IllegalArgumentException e) {
			LoginResponse loginResponse = new LoginResponse(username, SocialType.KAKAO);
			return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.REQUIRED_REGISTER, loginResponse));
		}
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse> register(
		final @RequestPart @Valid RegisterRequest registerRequest,
		final @RequestPart(required = false) MultipartFile profile,
		final HttpServletResponse httpServletResponse
	) {
		String username = userAuthenticationService.register(registerRequest, profile);

		Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_HEADER,
			userAuthenticationService.generateRefreshTokenByUsername(username));
		refreshTokenCookie.setHttpOnly(true);
		httpServletResponse.addCookie(refreshTokenCookie);

		AccessTokenResponse accessTokenResponse = new AccessTokenResponse(
			userAuthenticationService.generateAccessTokenByUsername(username));
		return ResponseEntity.ok(ApiResponse.ok(accessTokenResponse));
	}

	@GetMapping("/validate/nickname")
	public ResponseEntity<Void> validateNickname(final @RequestParam("value") String nickname) {
		userAuthenticationService.validateNickname(nickname);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<MemberResponse>> getUserInfo(final @LoginUsername String username) {
		return ResponseEntity.ok(ApiResponse.ok(MemberResponse.from(userService.getUserInfo(username))));
	}
}
