package com.polzzak.domain.coupon.dto;

import java.time.LocalDateTime;

public record CouponSummaryDto(String reward, LocalDateTime rewardedDate) {

	public static CouponSummaryDto from(String reward, LocalDateTime rewardedDate) {
		return new CouponSummaryDto(reward, rewardedDate);
	}
}
