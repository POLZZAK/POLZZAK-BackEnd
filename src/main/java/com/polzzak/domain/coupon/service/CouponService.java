package com.polzzak.domain.coupon.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.coupon.dto.CouponIssueRequest;
import com.polzzak.domain.coupon.entity.Coupon;
import com.polzzak.domain.coupon.repository.CouponRepository;
import com.polzzak.domain.stampboard.entity.StampBoard;
import com.polzzak.domain.stampboard.service.StampBoardService;
import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.global.exception.ErrorCode;
import com.polzzak.global.exception.PolzzakException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

	private final StampBoardService stampBoardService;
	private final UserService userService;
	private final CouponRepository couponRepository;

	@Transactional
	public void issueCoupon(MemberDto guardian, CouponIssueRequest couponIssueRequest) {
		StampBoard stampBoard = stampBoardService.getStampBoard(couponIssueRequest.stampBoardId());
		validateIssueCoupon(guardian, stampBoard);

		Member guardianEntity = userService.findMemberByMemberId(stampBoard.getGuardianId());
		Member kidEntity = userService.findMemberByMemberId(stampBoard.getKidId());
		LocalDateTime rewardDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(couponIssueRequest.rewardDate()),
			TimeZone.getDefault().toZoneId());

		Coupon coupon = couponRepository.save(Coupon.createCoupon()
			.guardian(guardianEntity)
			.kid(kidEntity)
			.stampBoard(stampBoard)
			.reward(stampBoard.getReward())
			.rewardDate(rewardDate)
			.build());
		stampBoard.issueCoupon(coupon.getRewardDate());
	}

	@Transactional
	public void rewardCoupon(MemberDto kid, long couponId) {
		Coupon coupon = couponRepository.getReferenceById(couponId);
		StampBoard stampBoard = coupon.getStampBoard();
		validateRewardCoupon(kid, stampBoard, coupon);

		stampBoard.rewardCoupon();
	}

	private void validateIssueCoupon(MemberDto guardian, StampBoard stampBoard) {
		if (stampBoard.isNotOwner(guardian.memberId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		if (!stampBoard.isCompleted()) {
			throw new IllegalArgumentException("stamp board is not completed.");
		}
	}

	private void validateRewardCoupon(MemberDto kid, StampBoard stampBoard, Coupon coupon) {
		if (coupon.isNotOwner(kid.memberId()) || stampBoard.isNotOwner(kid.memberId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		if (!stampBoard.isIssuedCoupon()) {
			throw new IllegalArgumentException("stamp board is not completed.");
		}
	}
}
