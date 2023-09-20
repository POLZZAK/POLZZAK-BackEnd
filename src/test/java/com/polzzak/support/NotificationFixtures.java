package com.polzzak.support;

import java.time.LocalDateTime;
import java.util.List;

import com.polzzak.domain.notification.dto.MemberDtoForNotification;
import com.polzzak.domain.notification.dto.NotificationDto;
import com.polzzak.domain.notification.dto.NotificationResponse;
import com.polzzak.domain.notification.dto.NotificationSettingDto;
import com.polzzak.domain.notification.dto.UpdateNotificationSetting;
import com.polzzak.domain.notification.entity.Notification;
import com.polzzak.domain.notification.entity.NotificationType;

public class NotificationFixtures {
	public static final MemberDtoForNotification MEMBER1 = new MemberDtoForNotification(1, "nick1", "url");
	public static final MemberDtoForNotification MEMBER2 = new MemberDtoForNotification(2, "nick2", "url");
	public static final List<NotificationDto> NOTIFICATION_DTO_LIST = List.of(
		new NotificationDto(1, NotificationType.CREATED_STAMP_BOARD, Notification.Status.UNREAD, "title1", "message1",
			MEMBER1, "link1", 22L, LocalDateTime.now()),
		new NotificationDto(1, NotificationType.ISSUED_COUPON, Notification.Status.READ, "title2", "message2", MEMBER2,
			"link2", 23L, LocalDateTime.now())
	);
	public static final NotificationResponse NOTIFICATION_RESPONSE = new NotificationResponse(null,
		NOTIFICATION_DTO_LIST);

	public static final NotificationSettingDto NOTIFICATION_SETTING_DTO = new NotificationSettingDto(true, true, true,
		false, false, true, true, false, true);

	public static final UpdateNotificationSetting UPDATE_NOTIFICATION_SETTING = new UpdateNotificationSetting(true,
		true, true, false, false, true, true, false, true);
}
