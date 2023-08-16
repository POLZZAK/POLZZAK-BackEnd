package com.polzzak.domain.notification.handler;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.polzzak.domain.notification.dto.NotificationCreateEvent;
import com.polzzak.domain.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class NotificationEventHandler {

	private final NotificationService notificationService;

	@EventListener
	public void addNotification(NotificationCreateEvent event) {
		try {
			notificationService.addNotification(event.senderId(), event.receiverId(), event.type(), event.data());
			log.info("[NotificationEvent] info. sender_id : {}, receiver_id : {}, type : {}, data : {}",
				event.senderId(), event.receiverId(), event.type(), event.data());
		} catch (Exception e) {
			log.error("[NotificationEvent] error.", e);
		}
	}
}
