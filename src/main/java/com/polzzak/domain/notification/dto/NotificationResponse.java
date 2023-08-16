package com.polzzak.domain.notification.dto;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.polzzak.domain.notification.entity.Notification;

public record NotificationResponse(Long startId, List<NotificationDto> notificationDtoList) {

	public static NotificationResponse from(Pageable pageable, List<NotificationDto> notifications, boolean hasNext) {
		if (hasNext) {
			return new NotificationResponse(notifications.get(pageable.getPageSize() - 1).id(),
				notifications);
		}
		return new NotificationResponse(null, notifications);
	}
}
