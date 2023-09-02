package com.polzzak.domain.coupon.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.coupon.entity.Coupon;
import com.polzzak.domain.coupon.repository.CouponRepository;
import com.polzzak.domain.notification.dto.NotificationCreateEvent;
import com.polzzak.domain.notification.entity.NotificationType;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class CouponScheduler {

	private final CouponRepository couponRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	public void couponRewardTask() {
		log.info("Coupon Reward Task Start");

		LocalDate yesterday = LocalDate.now().minusDays(1);
		LocalDate tomorrow = LocalDate.now().plusDays(1);

		List<Coupon> coupons = couponRepository.findByState(Coupon.CouponState.ISSUED);
		for (Coupon coupon : coupons) {
			LocalDate rewardDate = coupon.getRewardDate().toLocalDate();

			if (tomorrow.isEqual(rewardDate)) {
				// 보호자에게
				eventPublisher.publishEvent(
					new NotificationCreateEvent(coupon.getKid().getId(), coupon.getGuardian().getId(),
						NotificationType.REWARD_REQUEST_AGAIN, String.valueOf(coupon.getId())));
				// 아이에게
				eventPublisher.publishEvent(
					new NotificationCreateEvent(coupon.getGuardian().getId(), coupon.getKid().getId(),
						NotificationType.REWARDED_REQUEST, String.valueOf(coupon.getId())));
				continue;
			}

			if (yesterday.isEqual(rewardDate)) {
				eventPublisher.publishEvent(
					new NotificationCreateEvent(coupon.getKid().getId(), coupon.getGuardian().getId(),
						NotificationType.REWARD_FAIL, String.valueOf(coupon.getId())));
			}
		}

		log.info("Coupon Reward Task End");
	}
}
