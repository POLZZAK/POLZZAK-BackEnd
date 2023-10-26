package com.polzzak.domain.notification.dto;

public record NotificationResponseWithCount(NotificationResponse response, int unreadNotificationCount) {

	public static NotificationResponseWithCount from(NotificationResponse response, int unreadNotificationCount) {
		return new NotificationResponseWithCount(response, unreadNotificationCount);
	}
}
