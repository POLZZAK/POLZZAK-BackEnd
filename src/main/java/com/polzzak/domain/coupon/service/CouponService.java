package com.polzzak.domain.coupon.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.coupon.dto.CouponDto;
import com.polzzak.domain.coupon.dto.CouponListDto;
import com.polzzak.domain.coupon.entity.Coupon;
import com.polzzak.domain.coupon.repository.CouponRepository;
import com.polzzak.domain.family.dto.FamilyMemberDto;
import com.polzzak.domain.family.service.FamilyMapService;
import com.polzzak.domain.notification.dto.NotificationCreateEvent;
import com.polzzak.domain.notification.entity.NotificationType;
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
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public void issueCoupon(final Long kidId, final long stampBoardId) {
		StampBoard stampBoard = stampBoardService.getStampBoard(stampBoardId);
		Member kid = userService.findMemberByMemberIdWithMemberType(kidId);
		validateIssueCoupon(kid, stampBoard);

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

	public List<CouponListDto> getCouponList(final Long memberId, final Long partnerMemberId,
		final Coupon.CouponState couponState) {
		Member member = userService.findMemberByMemberIdWithMemberType(memberId);
		List<FamilyMemberDto> targetFamilies = getTargetFamilies(familyMapService.getMyFamilies(member.getId()),
			partnerMemberId);

		return getCouponListDtos(member, targetFamilies, couponState);
	}

	public CouponDto getCoupon(final Long memberId, final long couponId) {
		Member member = userService.findMemberByMemberId(memberId);
		Coupon coupon = couponRepository.findById(couponId).orElse(null);
		if (coupon == null) {
			throw new PolzzakException(ErrorCode.TARGET_NOT_EXIST);
		}
		validateCouponOwner(coupon, member);

		MemberResponse guardianResponse = userService.getMemberResponse(coupon.getGuardian().getId());
		MemberResponse kidResponse = userService.getMemberResponse(coupon.getKid().getId());
		return CouponDto.from(coupon, coupon.getStampBoard(), guardianResponse, kidResponse);
	}

	@Transactional
	public void receiveReward(final Long kidId, final long couponId) {
		Member kid = userService.findMemberByMemberIdWithMemberType(kidId);
		Coupon coupon = couponRepository.getReferenceById(couponId);
		validateCouponOwner(coupon, kid);

		coupon.receiveReward();

		eventPublisher.publishEvent(
			new NotificationCreateEvent(kidId, coupon.getGuardian().getId(), NotificationType.REWARDED,
				String.valueOf(couponId)));
	}

	@Transactional
	public void requestReward(final Long kidId, final long couponId) {
		Coupon coupon = couponRepository.getReferenceById(couponId);
		if (!coupon.isPossibleRequest()) {
			throw new IllegalArgumentException("선물 조르기는 1시간 마다 가능합니다.");
		}

		coupon.requestReward();
		eventPublisher.publishEvent(
			new NotificationCreateEvent(kidId, coupon.getGuardian().getId(), NotificationType.REWARD_REQUEST,
				String.valueOf(couponId)));
	}

	private List<FamilyMemberDto> getTargetFamilies(final List<FamilyMemberDto> allFamilies,
		final Long partnerMemberId) {
		if (partnerMemberId == null) {
			return allFamilies;
		}
		return allFamilies.stream()
			.filter(familyMemberDto -> familyMemberDto.memberId() == partnerMemberId)
			.toList();
	}

	private List<CouponListDto> getCouponListDtos(final Member member, final List<FamilyMemberDto> families,
		Coupon.CouponState couponState) {
		return families.stream()
			.map(family -> CouponListDto.from(family, getCoupons(member, family.memberId(), couponState)))
			.toList();
	}

	private List<Coupon> getCoupons(final Member member, final long familyMemberId,
		final Coupon.CouponState couponState) {
		if (member.isKid()) {
			return couponRepository.findByGuardianIdAndKidIdAndState(familyMemberId, member.getId(), couponState);
		}
		return couponRepository.findByGuardianIdAndKidIdAndState(member.getId(), familyMemberId, couponState);
	}

	private void validateCouponOwner(final Coupon coupon, final Member member) {
		if (coupon.isNotOwner(member)) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
	}

	private void validateIssueCoupon(final Member kid, final StampBoard stampBoard) {
		if (!kid.isKid() || stampBoard.isNotOwner(kid.getId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		if (!stampBoard.isIssuedCoupon()) {
			throw new IllegalArgumentException("stamp board is not issued coupon.");
		}
	}
}
