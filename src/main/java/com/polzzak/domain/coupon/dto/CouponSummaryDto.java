package com.polzzak.domain.coupon.dto;

import java.time.LocalDateTime;

public record CouponSummaryDto(
	long couponId, String reward, LocalDateTime rewardRequestDate, LocalDateTime rewardDate
) {

	public static CouponSummaryDto from(long couponId, String reward, LocalDateTime rewardDate) {
		return new CouponSummaryDto(couponId, reward, LocalDateTime.now().minusHours(2), rewardDate);
	}
}
