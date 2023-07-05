package com.polzzak.domain.coupon.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.polzzak.domain.coupon.dto.CouponIssueRequest;
import com.polzzak.domain.coupon.service.CouponService;
import com.polzzak.domain.stampboard.service.StampBoardService;
import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.security.LoginUsername;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

	private final CouponService couponService;
	private final UserService userService;

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createCoupon(
		@LoginUsername String username, @RequestBody CouponIssueRequest couponIssueRequest
	) {
		MemberDto guardian = userService.getGuardianInfo(username);
		couponService.issueCoupon(guardian, couponIssueRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created());
	}

	@PostMapping("/{couponId}/reward")
	public ResponseEntity<Void> rewardCoupon(
		@LoginUsername String username, @PathVariable long couponId
	) {
		MemberDto kid = userService.getKidInfo(username);
		couponService.rewardCoupon(kid, couponId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
