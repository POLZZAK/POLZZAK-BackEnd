package com.polzzak.domain.coupon.dto;

import java.util.List;

import com.polzzak.domain.coupon.entity.Coupon;
import com.polzzak.domain.family.dto.FamilyMemberDto;

public record CouponListDto(FamilyMemberDto family, List<CouponSummaryDto> coupons) {

	public static CouponListDto from(FamilyMemberDto family, List<Coupon> coupons) {
		List<CouponSummaryDto> couponSummaryDtoList = coupons.stream()
			.map(coupon -> CouponSummaryDto.from(coupon.getReward(), coupon.getRewardDate()))
			.toList();
		return new CouponListDto(family, couponSummaryDtoList);
	}
}
