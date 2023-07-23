package com.polzzak.domain.coupon.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.coupon.dto.CouponDto;
import com.polzzak.domain.coupon.dto.CouponListDto;
import com.polzzak.domain.coupon.entity.Coupon;
import com.polzzak.domain.coupon.repository.CouponRepository;
import com.polzzak.domain.family.service.FamilyMapService;
import com.polzzak.domain.stampboard.entity.StampBoard;
import com.polzzak.domain.stampboard.service.StampBoardService;
import com.polzzak.domain.user.dto.MemberResponse;
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
	public void issueCoupon(Long guardianId, long stampBoardId) {
		StampBoard stampBoard = stampBoardService.getStampBoard(stampBoardId);
		Member guardian = userService.findMemberByMemberIdWithMemberType(guardianId);
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

	public List<CouponListDto> getCouponList(Long memberId, Coupon.CouponState couponState) {
		Member member = userService.findMemberByMemberIdWithMemberType(memberId);

		return familyMapService.getMyFamilies(member.getId()).stream()
			.map(family -> CouponListDto.from(family, getCoupons(member, family.memberId(), couponState)))
			.toList();
	}

	public CouponDto getCoupon(Long memberId, long couponId) {
		Member member = userService.findMemberByMemberId(memberId);
		Coupon coupon = couponRepository.getReferenceById(couponId);
		validateCouponOwner(coupon, member);

		MemberResponse guardianResponse = userService.getMemberResponse(coupon.getGuardian().getId());
		MemberResponse kidResponse = userService.getMemberResponse(coupon.getKid().getId());
		return CouponDto.from(coupon, coupon.getStampBoard(), guardianResponse, kidResponse);
	}

	@Transactional
	public void receiveReward(Long kidId, long couponId) {
		Member kid = userService.findMemberByMemberIdWithMemberType(kidId);
		Coupon coupon = couponRepository.getReferenceById(couponId);
		validateCouponOwner(coupon, kid);

		coupon.receiveReward();
	}

	private List<Coupon> getCoupons(Member member, long familyMemberId, Coupon.CouponState couponState) {
		if (member.isKid()) {
			return couponRepository.findByGuardianIdAndState(familyMemberId, couponState);
		}
		return couponRepository.findByKidIdAndState(familyMemberId, couponState);
	}

	private void validateCouponOwner(Coupon coupon, Member member) {
		if (coupon.isNotOwner(member)) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
	}

	private void validateIssueCoupon(Member guardian, StampBoard stampBoard) {
		if (!guardian.isGuardian() || stampBoard.isNotOwner(guardian.getId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		if (!stampBoard.isIssuedCoupon()) {
			throw new IllegalArgumentException("stamp board is not issued coupon.");
		}
	}
}