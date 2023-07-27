package com.polzzak.domain.coupon.dto;

import java.time.LocalDateTime;

public record CouponSummaryDto(
	long couponId, String reward, LocalDateTime rewardRequestDate, LocalDateTime rewardDate
) {

	public static CouponSummaryDto from(final long couponId, final String reward, final LocalDateTime rewardDate) {
		return new CouponSummaryDto(couponId, reward, rewardDate.minusHours(2), rewardDate);
	}
}
