package com.polzzak.domain.coupon.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.polzzak.domain.coupon.dto.CouponDto;
import com.polzzak.domain.coupon.dto.CouponListDto;
import com.polzzak.domain.coupon.dto.StampBoardForIssueCoupon;
import com.polzzak.domain.coupon.entity.Coupon;
import com.polzzak.domain.coupon.service.CouponService;
import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.security.LoginId;
import com.polzzak.global.security.LoginUsername;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

	private final CouponService couponService;
	private final UserService userService;

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> issueCoupon(
		@LoginId Long memberId, @RequestBody StampBoardForIssueCoupon stampBoardForIssueCoupon
	) {
		couponService.issueCoupon(memberId, stampBoardForIssueCoupon.stampBoardId());
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created());
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<CouponListDto>>> getCoupons(
		@LoginId Long memberId, @RequestParam("couponState") String couponStateAsStr) {
		Coupon.CouponState couponState = Coupon.CouponState.valueOf(couponStateAsStr.toUpperCase());
		return ResponseEntity.ok(ApiResponse.ok(couponService.getCouponList(memberId, couponState)));
	}

	@GetMapping("/{couponId}")
	public ResponseEntity<ApiResponse<CouponDto>> getCoupon(
		@LoginId Long memberId, @PathVariable long couponId) {
		return ResponseEntity.ok(ApiResponse.ok(couponService.getCoupon(memberId, couponId)));
	}

	@PostMapping("/{couponId}/receive")
	public ResponseEntity<Void> receiveCoupon(
		@LoginId Long memberId, @PathVariable long couponId) {
		couponService.receiveReward(memberId, couponId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}