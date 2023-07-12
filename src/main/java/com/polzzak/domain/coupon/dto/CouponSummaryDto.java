package com.polzzak.domain.coupon.dto;

import java.time.LocalDateTime;

public record CouponSummaryDto(String reward, LocalDateTime rewardDate) {

	public static CouponSummaryDto from(String reward, LocalDateTime rewardDate) {
		return new CouponSummaryDto(reward, rewardDate);
	}
}
