package com.polzzak.domain.notification.dto;

import com.polzzak.domain.notification.entity.NotificationType;

public record NotificationCreateEvent(Long senderId, Long receiverId, NotificationType type, String data) {
}
