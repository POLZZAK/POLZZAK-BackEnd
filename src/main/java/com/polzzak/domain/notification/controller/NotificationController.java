package com.polzzak.domain.notification.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.polzzak.domain.notification.dto.NotificationResponseWithCount;
import com.polzzak.domain.notification.dto.NotificationSettingDto;
import com.polzzak.domain.notification.dto.ReadNotificationId;
import com.polzzak.domain.notification.dto.UpdateNotificationSetting;
import com.polzzak.domain.notification.entity.Notification;
import com.polzzak.domain.notification.service.NotificationService;
import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.security.LoginId;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

	private final NotificationService notificationService;

	private static final int NOTIFICATION_PAGE_SIZE = 10;
	private final String longMaxValue = "9223372036854775807";

	@GetMapping
	public ResponseEntity<ApiResponse<NotificationResponseWithCount>> getNotifications(final @LoginId Long memberId,
		@RequestParam(required = false, defaultValue = longMaxValue) final long startId) {
		return ResponseEntity.ok(
			ApiResponse.ok(
				notificationService.getNotificationsAndChangeStatus(memberId, NOTIFICATION_PAGE_SIZE, startId)));
	}

	@DeleteMapping
	public ResponseEntity<ApiResponse<Void>> deleteNotifications(
		@RequestParam final List<Long> notificationIds
	) {
		notificationService.deleteNotifications(notificationIds);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PostMapping("/read")
	public ResponseEntity<ApiResponse<Integer>> readNotification(
		final @LoginId Long memberId,
		final @RequestBody ReadNotificationId readNotificationId) {
		notificationService.changeNotificationStatus(readNotificationId.notificationId(), Notification.Status.READ);
		return ResponseEntity.ok(ApiResponse.ok(notificationService.getUnreadNotificationCount(memberId)));
	}

	@GetMapping("/settings")
	public ResponseEntity<ApiResponse<NotificationSettingDto>> getNotificationSettings(final @LoginId Long memberId) {
		return ResponseEntity.ok(ApiResponse.ok(notificationService.getNotificationSetting(memberId)));
	}

	@PatchMapping("/settings")
	public ResponseEntity<Void> updateNotificationSettings(final @LoginId Long memberId,
		final @RequestBody UpdateNotificationSetting updateNotificationSetting) {
		notificationService.updateNotificationSetting(memberId, updateNotificationSetting);
		return ResponseEntity.noContent().build();
	}
}
