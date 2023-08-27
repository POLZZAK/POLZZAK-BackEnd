package com.polzzak.domain.coupon.dto;

import java.util.List;

import com.polzzak.domain.coupon.entity.Coupon;
import com.polzzak.domain.family.dto.FamilyMemberDto;

public record CouponListDto(FamilyMemberDto family, List<CouponSummaryDto> coupons) {

	public static CouponListDto from(final FamilyMemberDto family, final List<Coupon> coupons) {
		List<CouponSummaryDto> couponSummaryDtoList = coupons.stream()
			.map(CouponSummaryDto::from)
			.toList();
		return new CouponListDto(family, couponSummaryDtoList);
	}
}
