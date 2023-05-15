package com.polzzak.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.polzzak.domain.user.dto.MemberResponse;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.security.LoginUsername;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

	private final UserService userService;

	public UserRestController(final UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<MemberResponse>> getMemberInfo(final @LoginUsername String username) {
		return ResponseEntity.ok(ApiResponse.ok(MemberResponse.from(userService.getMemberInfo(username))));
	}
}
