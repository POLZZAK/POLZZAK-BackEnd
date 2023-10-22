package com.polzzak.domain.notification.handler;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.polzzak.domain.notification.dto.NotificationCreateEvent;
import com.polzzak.domain.notification.dto.NotificationDto;
import com.polzzak.domain.notification.dto.NotificationSettingDto;
import com.polzzak.domain.notification.entity.Notification;
import com.polzzak.domain.notification.entity.NotificationType;
import com.polzzak.domain.notification.service.NotificationService;
import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.global.infra.firebase.FirebaseCloudMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class NotificationEventHandler {

	private final NotificationService notificationService;
	private final FirebaseCloudMessageService firebaseCloudMessageService;
	private final UserService userService;

	@Async
	@EventListener
	public void addNotification(NotificationCreateEvent event) {
		try {
			if (event.type() == NotificationType.FAMILY_REQUEST_COMPLETE) {
				notificationService.changeRequestNotificationStatus(event.senderId(), event.receiverId(),
					Notification.Status.REQUEST_FAMILY_ACCEPT);
			}
			if (event.type() == NotificationType.FAMILY_REQUEST_REJECT) {
				notificationService.changeRequestNotificationStatus(event.senderId(), event.receiverId(),
					Notification.Status.REQUEST_FAMILY_REJECT);
				//기존 알림 타입 변경용 타입
				return;
			}

			NotificationSettingDto notificationSetting = notificationService.getNotificationSetting(event.receiverId());
			switch (event.type()) {
				case FAMILY_REQUEST, FAMILY_REQUEST_COMPLETE -> {
					if (!notificationSetting.familyRequest()) {
						return;
					}
				}
				case LEVEL_UP, LEVEL_DOWN -> {
					if (!notificationSetting.level()) {
						return;
					}
				}
				case STAMP_REQUEST -> {
					if (!notificationSetting.stampRequest()) {
						return;
					}
				}
				case REWARD_REQUEST -> {
					if (!notificationSetting.rewardRequest()) {
						return;
					}
				}
				case STAMP_BOARD_COMPLETE -> {
					if (!notificationSetting.stampBoardComplete()) {
						return;
					}
				}
				case REWARDED -> {
					if (!notificationSetting.rewarded()) {
						return;
					}
				}
				case REWARD_FAIL -> {
					if (!notificationSetting.rewardFail()) {
						return;
					}
				}
				case CREATED_STAMP_BOARD -> {
					if (!notificationSetting.createdStampBoard()) {
						return;
					}
				}
				case ISSUED_COUPON -> {
					if (!notificationSetting.issuedCoupon()) {
						return;
					}
				}
				case REWARDED_REQUEST -> {
					if (!notificationSetting.rewardedRequest()) {
						return;
					}
				}
				default -> {
				}
			}

			Notification notification = notificationService.addNotification(event.senderId(), event.receiverId(),
				event.type(), event.data());
			sendPushNotification(event.receiverId(), notification);

			log.info("[NotificationEvent] info. sender_id : {}, receiver_id : {}, type : {}, data : {}",
				event.senderId(), event.receiverId(), event.type(), event.data());
		} catch (Exception e) {
			log.error("[NotificationEvent] error.", e);
		}
	}

	private void sendPushNotification(long memberId,  Notification notification) {
		Member member = userService.findMemberByMemberId(memberId);
		NotificationDto notificationDto = notificationService.getNotificationDto(member, notification, false);

		firebaseCloudMessageService.sendPushNotification(member, notificationDto.title(), notificationDto.message(),
			notificationDto.link());
	}
}
