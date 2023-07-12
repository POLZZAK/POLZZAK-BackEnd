package com.polzzak.domain.coupon.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.coupon.dto.CouponDto;
import com.polzzak.domain.coupon.dto.CouponListDto;
import com.polzzak.domain.coupon.entity.Coupon;
import com.polzzak.domain.coupon.repository.CouponRepository;
import com.polzzak.domain.family.dto.FamilyMemberDto;
import com.polzzak.domain.family.service.FamilyMapService;
import com.polzzak.domain.stampboard.entity.StampBoard;
import com.polzzak.domain.stampboard.service.StampBoardService;
import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.domain.user.dto.MemberResponse;
import com.polzzak.domain.user.dto.MemberTypeDto;
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
	private final FamilyMapService familyMapService;
	private final CouponRepository couponRepository;

	@Transactional
	public void issueCoupon(MemberDto guardian, long stampBoardId) {
		StampBoard stampBoard = stampBoardService.getStampBoard(stampBoardId);
		validateIssueCoupon(guardian, stampBoard);

		Member guardianEntity = userService.findMemberByMemberId(stampBoard.getGuardianId());
		Member kidEntity = userService.findMemberByMemberId(stampBoard.getKidId());
		LocalDateTime rewardDate = stampBoard.getRewardDate();

		Coupon coupon = Coupon.createCoupon()
			.guardian(guardianEntity)
			.kid(kidEntity)
			.stampBoard(stampBoard)
			.reward(stampBoard.getReward())
			.rewardDate(rewardDate)
			.build();
		couponRepository.save(coupon);
		stampBoard.rewardCoupon();
	}

	public List<CouponListDto> getCouponList(MemberDto member, Coupon.CouponState couponState) {
		List<CouponListDto> result = new ArrayList<>();
		for (FamilyMemberDto family : familyMapService.getMyFamilies(member.memberId())) {
			List<Coupon> coupons;
			if (MemberTypeDto.isKid(member.memberType())) {
				coupons = couponRepository.findByGuardianIdAndState(family.memberId(), couponState);
			} else {
				coupons = couponRepository.findByKidIdAndState(family.memberId(), couponState);
			}

			result.add(CouponListDto.from(family, coupons));
		}

		return result;
	}

	public CouponDto getCoupon(MemberDto member, long couponId) {
		Coupon coupon = couponRepository.getReferenceById(couponId);
		if (coupon.isNotOwner(member)) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}

		MemberResponse guardianResponse = userService.getMemberResponse(coupon.getGuardian().getId());
		MemberResponse kidResponse = userService.getMemberResponse(coupon.getKid().getId());
		return CouponDto.from(coupon, coupon.getStampBoard(), guardianResponse, kidResponse);
	}

	@Transactional
	public void receiveReward(MemberDto kid, long couponId) {
		Coupon coupon = couponRepository.getReferenceById(couponId);
		if (coupon.isNotOwner(kid)) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}

		coupon.receiveReward();
	}

	private void validateIssueCoupon(MemberDto guardian, StampBoard stampBoard) {
		if (stampBoard.isNotOwner(guardian.memberId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		if (!stampBoard.isIssuedCoupon()) {
			throw new IllegalArgumentException("stamp board is not issued coupon.");
		}
	}
}
