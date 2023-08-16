package com.polzzak.domain.notification.dto;

import com.polzzak.domain.notification.entity.Notification;

public record NotificationDto(
	long id, Notification.Status status, String title, String message, MemberDtoForNotification sender, String link,
	Long requestFamilyId
) {

	public static NotificationDto from(final Notification notification, final String message, final String link,
		final MemberDtoForNotification sender) {
		return new NotificationDto(notification.getId(), notification.getStatus(), notification.getType().getTitle(),
			message, sender, link, notification.getRequestFamilyId());
	}
}
