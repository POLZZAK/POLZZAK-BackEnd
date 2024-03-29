package com.polzzak.domain.notification.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.coupon.dto.CouponDto;
import com.polzzak.domain.coupon.service.CouponService;
import com.polzzak.domain.notification.dto.MemberDtoForNotification;
import com.polzzak.domain.notification.dto.NotificationDto;
import com.polzzak.domain.notification.dto.NotificationResponse;
import com.polzzak.domain.notification.dto.NotificationResponseWithCount;
import com.polzzak.domain.notification.dto.NotificationSettingDto;
import com.polzzak.domain.notification.dto.UpdateNotificationSetting;
import com.polzzak.domain.notification.entity.Notification;
import com.polzzak.domain.notification.entity.NotificationSetting;
import com.polzzak.domain.notification.entity.NotificationType;
import com.polzzak.domain.notification.repository.NotificationRepository;
import com.polzzak.domain.notification.repository.NotificationSettingRepository;
import com.polzzak.domain.stampboard.entity.StampBoard;
import com.polzzak.domain.stampboard.service.StampBoardService;
import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.global.infra.file.FileClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

	private final UserService userService;
	private final StampBoardService stampBoardService;
	private final CouponService couponService;
	private final FileClient fileClient;
	private final NotificationRepository notificationRepository;
	private final NotificationSettingRepository notificationSettingRepository;

	@Transactional
	public Notification addNotification(final Long senderId, final Long receiverId, final NotificationType type,
		final String data) {
		Member sender = userService.findMemberByMemberId(senderId);
		Member receiver = userService.findMemberByMemberId(receiverId);

		Notification notification = Notification.createNotification()
			.sender(sender)
			.receiver(receiver)
			.type(type)
			.data(data)
			.build();
		notificationRepository.save(notification);
		return notification;
	}

	@Transactional
	public NotificationResponseWithCount getNotificationsAndChangeStatus(final Long memberId, final int size,
		final long startId) {
		NotificationResponse notificationResponse = getNotificationResponse(memberId, size, startId);

		List<Long> notificationIds = notificationResponse.notificationDtoList().stream()
			.filter(notificationDto -> notificationDto.type() != NotificationType.FAMILY_REQUEST)
			.map(NotificationDto::id)
			.toList();
		notificationRepository.updateStatusByIds(notificationIds, Notification.Status.READ);
		int count = notificationRepository.countByStatusIsUnRead(memberId);
		return NotificationResponseWithCount.from(notificationResponse, count);
	}

	public int getUnreadNotificationCount(final long memberId) {
		return notificationRepository.countByStatusIsUnRead(memberId);
	}

	@Transactional
	public void changeRequestNotificationStatus(final Long senderId, final Long receiverId,
		final Notification.Status status) {
		Long notificationId = notificationRepository.selectIdBySenderIdAndReceiverIdAndStatus(senderId, receiverId);
		notificationRepository.updateStatusByIds(List.of(notificationId), status);
	}

	@Transactional
	public void changeNotificationStatus(final long notificationId, final Notification.Status status) {
		notificationRepository.updateStatusById(notificationId, status);
	}

	@Transactional
	public void deleteNotifications(final List<Long> notificationIds) {
		List<Notification> notifications = notificationRepository.findByIdIn(notificationIds);
		List<Long> filteredNotificationIds = notifications.stream()
			.filter(notification -> notification.getType() != NotificationType.FAMILY_REQUEST
				|| notification.getStatus() != Notification.Status.REQUEST_FAMILY)
			.map(Notification::getId)
			.toList();

		if (filteredNotificationIds.isEmpty()) {
			return;
		}

		notificationRepository.deleteByIdIn(filteredNotificationIds);
	}

	public NotificationSettingDto getNotificationSetting(final Long memberId) {
		Member member = userService.findMemberByMemberId(memberId);
		NotificationSetting notificationSetting = notificationSettingRepository.findByMember(member);

		return NotificationSettingDto.from(notificationSetting);
	}

	@Transactional
	public void createNotificationSetting(final Long memberId) {
		Member member = userService.findMemberByMemberId(memberId);
		NotificationSetting notificationSetting = NotificationSetting.createNotificationSetting()
			.member(member)
			.build();

		notificationSettingRepository.save(notificationSetting);
	}

	@Transactional
	public void updateNotificationSetting(final Long memberId,
		final UpdateNotificationSetting updateNotificationSetting) {
		Member member = userService.findMemberByMemberId(memberId);
		notificationSettingRepository.findByMember(member).updateNotificationSetting(updateNotificationSetting);
	}

	private NotificationResponse getNotificationResponse(final Long memberId, final int size,
		final long startId) {
		Member member = userService.findMemberByMemberId(memberId);
		PageRequest pageRequest = PageRequest.of(0, size, Sort.by("id").descending());

		Slice<Notification> notifications = notificationRepository.findNotificationsByReceiverIdAndIdLessThan(
			memberId, startId,
			pageRequest);

		List<NotificationDto> notificationDtoList = notifications.getContent().stream()
			.map(notification -> getNotificationDto(member, notification, true))
			.toList();

		return NotificationResponse.from(pageRequest, notificationDtoList, notifications.hasNext());
	}

	public NotificationDto getNotificationDto(final Member member, final Notification notification,
		final boolean isBold) {
		Member sender = notification.getSender();
		MemberDtoForNotification senderDto = sender == null ? null : MemberDtoForNotification.from(sender,
			fileClient.getSignedUrl(sender.getProfileKey()));
		String message;
		if (isBold) {
			message = notification.getType()
				.getMessageWithParameter(getMessageParameter(member.getId(), notification));
		} else {
			message = notification.getType()
				.getParameterWithoutBold(getMessageParameter(member.getId(), notification));
		}

		String link = notification.getType().getLinkWithParameter(getLinkParameter(member.getId(), notification));

		return NotificationDto.from(notification, message, link, senderDto);
	}

	private String getMessageParameter(final Long memberId, final Notification notification) {
		String data = notification.getData();

		return switch (notification.getType()) {
			case FAMILY_REQUEST, FAMILY_REQUEST_COMPLETE -> notification.getSender().getNickname();
			case LEVEL_UP, LEVEL_DOWN -> data;
			case STAMP_REQUEST, STAMP_BOARD_COMPLETE, CREATED_STAMP_BOARD, ISSUED_COUPON -> {
				StampBoard stampBoard = stampBoardService.getStampBoardIncludeDeleted(Long.parseLong(data));
				yield stampBoard.getName();
			}
			case REWARD_REQUEST, REWARDED, REWARD_REQUEST_AGAIN, REWARD_FAIL, REWARDED_REQUEST -> {
				CouponDto coupon = couponService.getCoupon(memberId, Long.parseLong(data));
				yield coupon.reward();
			}
			default -> null;
		};
	}

	private String getLinkParameter(final Long memberId, final Notification notification) {
		String data = notification.getData();

		return switch (notification.getType()) {
			case STAMP_REQUEST, STAMP_BOARD_COMPLETE, CREATED_STAMP_BOARD, ISSUED_COUPON -> {
				StampBoard stampBoard = stampBoardService.getStampBoardIncludeDeleted(Long.parseLong(data));
				yield String.valueOf(stampBoard.getId());
			}
			case REWARD_REQUEST, REWARDED, REWARD_REQUEST_AGAIN, REWARD_FAIL, REWARDED_REQUEST -> {
				CouponDto coupon = couponService.getCoupon(memberId, Long.parseLong(data));
				yield String.valueOf(coupon.couponId());
			}
			default -> null;
		};
	}
}
