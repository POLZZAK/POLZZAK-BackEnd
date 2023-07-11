package com.polzzak.domain.coupon.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.polzzak.domain.coupon.entity.Coupon;
import com.polzzak.domain.stampboard.entity.Mission;
import com.polzzak.domain.stampboard.entity.StampBoard;
import com.polzzak.domain.user.dto.MemberResponse;

public record CouponDto(
	long couponId, String reward, CouponMember guardian, CouponMember kid, List<String> missionContents, int stampCount,
	Coupon.CouponState state, LocalDateTime startDate, LocalDateTime endDate
) {

	public static CouponDto from(Coupon coupon, StampBoard stampBoard, MemberResponse guardianResponse,
		MemberResponse kidResponse) {
		List<String> missionContents = stampBoard.getMissions().stream()
			.map(Mission::getContent)
			.toList();

		return new CouponDto(coupon.getId(), coupon.getReward(), CouponMember.from(guardianResponse),
			CouponMember.from(kidResponse), missionContents, stampBoard.getGoalStampCount(), coupon.getState(),
			stampBoard.getCreatedDate(), stampBoard.getCompletedDate());
	}

	public record CouponMember(String nickname, String profileUrl) {
		public static CouponMember from(MemberResponse memberResponse) {
			return new CouponMember(memberResponse.nickname(), memberResponse.profileUrl());
		}
	}
}
