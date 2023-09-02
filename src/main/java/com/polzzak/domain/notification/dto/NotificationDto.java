package com.polzzak.domain.notification.dto;

import java.time.LocalDateTime;

import com.polzzak.domain.notification.entity.Notification;
import com.polzzak.domain.notification.entity.NotificationType;

public record NotificationDto(
	long id, NotificationType type, Notification.Status status, String title, String message,
	MemberDtoForNotification sender, String link, Long requestFamilyId, LocalDateTime createdDate
) {

	public static NotificationDto from(final Notification notification, final String message, final String link,
		final MemberDtoForNotification sender) {
		return new NotificationDto(notification.getId(), notification.getType(), notification.getStatus(),
			notification.getType().getTitle(), message, sender, link, notification.getRequestFamilyId(),
			notification.getCreatedDate());
	}
}
