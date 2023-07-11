package com.polzzak.domain.coupon.dto;

import java.util.List;

import com.polzzak.domain.coupon.entity.Coupon;
import com.polzzak.domain.user.dto.MemberDto;

public record CouponListDto(MemberDto guardian, List<CouponSummaryDto> coupons) {

	public static CouponListDto from(MemberDto guardian, List<Coupon> coupons) {
		List<CouponSummaryDto> couponSummaryDtoList = coupons.stream()
			.map(coupon -> CouponSummaryDto.from(coupon.getReward(), coupon.getRewardDate()))
			.toList();
		return new CouponListDto(guardian, couponSummaryDtoList);
	}
}
