package com.polzzak.domain.notification.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	Slice<Notification> findNotificationsByReceiverIdAndIdLessThan(final long memberId, final long startId,
		final Pageable pageable);
}
