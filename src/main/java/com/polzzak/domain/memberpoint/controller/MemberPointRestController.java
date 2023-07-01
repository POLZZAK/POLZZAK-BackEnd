package com.polzzak.domain.memberpoint.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.polzzak.domain.memberpoint.dto.MemberPointHistorySliceResponse;
import com.polzzak.domain.memberpoint.dto.MemberPointResponse;
import com.polzzak.domain.memberpoint.service.MemberPointService;
import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.security.LoginId;

@RestController
@RequestMapping("/api/v1/member-points")
public class MemberPointRestController {
	private final MemberPointService memberPointService;

	public MemberPointRestController(final MemberPointService memberPointService) {
		this.memberPointService = memberPointService;
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<MemberPointResponse>> getMyMemberPoint(@LoginId final Long memberId) {
		return ResponseEntity.ok(ApiResponse.ok(memberPointService.getMyMemberPoint(memberId)));
	}

	@GetMapping("/earning-histories/me")
	public ResponseEntity<ApiResponse<MemberPointHistorySliceResponse>> getMyEarningHistories(
		@LoginId final Long memberId,
		@RequestParam(required = false, defaultValue = "0") final long startId,
		@RequestParam(required = false, defaultValue = "10") final int size
	) {
		return ResponseEntity.ok(ApiResponse.ok(memberPointService.getMyEarningHistories(memberId, startId, size)));
	}
}
