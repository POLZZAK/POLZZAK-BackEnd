package com.polzzak.domain.pushtoken.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.polzzak.domain.pushtoken.model.CreatePushToken;
import com.polzzak.domain.pushtoken.service.PushTokenService;
import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.security.LoginId;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/push-token")
public class PushTokenController {

	private final PushTokenService pushTokenService;

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createStampBoard(
		@LoginId Long memberId, @RequestBody @Valid CreatePushToken createPushToken
	) {
		pushTokenService.addToken(memberId, createPushToken.token());
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created());
	}
}
