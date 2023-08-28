package com.polzzak.domain.coupon.dto;

import java.time.LocalDateTime;

import com.polzzak.domain.coupon.entity.Coupon;

public record CouponSummaryDto(
	long couponId, String reward, LocalDateTime rewardRequestDate, LocalDateTime rewardDate
) {

	public static CouponSummaryDto from(final Coupon coupon) {
		return new CouponSummaryDto(coupon.getId(), coupon.getReward(), coupon.getRequestDate(),
			coupon.getRewardDate());
	}
}
