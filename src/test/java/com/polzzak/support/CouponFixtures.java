package com.polzzak.support;

import static com.polzzak.support.FamilyFixtures.*;
import static com.polzzak.support.UserFixtures.*;

import java.time.LocalDateTime;
import java.util.List;

import com.polzzak.domain.coupon.dto.CouponDto;
import com.polzzak.domain.coupon.dto.CouponListDto;
import com.polzzak.domain.coupon.dto.StampBoardForIssueCoupon;
import com.polzzak.domain.coupon.entity.Coupon;
import com.polzzak.domain.user.dto.MemberDto;

public class CouponFixtures {
	public static final MemberDto GUARDIAN = new MemberDto(1L, "보호자", MEMBER_GUARDIAN_TYPE_DTO);
	public static final MemberDto KID = new MemberDto(1L, "아이", MEMBER_GUARDIAN_TYPE_DTO);

	public static final StampBoardForIssueCoupon STAMP_BOARD_FOR_ISSUE_COUPON = new StampBoardForIssueCoupon(1);
	public static final List<Coupon> COUPONS_FOR_LIST = List.of(
		Coupon.createCoupon().reward("상입니다").rewardDate(LocalDateTime.now()).build(),
		Coupon.createCoupon().reward("선물입니다.").rewardDate(LocalDateTime.now()).build());
	public static final List<CouponListDto> COUPON_LIST_DTO_LIST = List.of(
		CouponListDto.from(FAMILY_GUARDIAN_MEMBER_DTO, COUPONS_FOR_LIST));
	public static final CouponDto COUPON_DTO = new CouponDto(1, "상상상", new CouponDto.CouponMember("보호자", "profile url"),
		new CouponDto.CouponMember("아이", "profile url"), List.of("미션1", "미션2", "미션3"), 10, Coupon.CouponState.ISSUED,
		LocalDateTime.now(), LocalDateTime.now());
}
