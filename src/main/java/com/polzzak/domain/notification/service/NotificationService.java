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
import com.polzzak.domain.notification.entity.Notification;
import com.polzzak.domain.notification.entity.NotificationType;
import com.polzzak.domain.notification.repository.NotificationRepository;
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

	@Transactional
	public void addNotification(final Long senderId, final Long receiverId, final NotificationType type,
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
	}

	public NotificationResponse getNotificationResponse(final Long memberId, final int size,
		final long startId) {
		Member member = userService.findMemberByMemberId(memberId);
		PageRequest pageRequest = PageRequest.of(0, size, Sort.by("id").descending());

		Slice<Notification> notifications = notificationRepository.findNotificationsByReceiverIdAndIdLessThan(
			memberId, startId,
			pageRequest);

		List<NotificationDto> notificationDtoList = notifications.getContent().stream()
			.map(notification -> getNotificationDto(member, notification))
			.toList();

		return NotificationResponse.from(pageRequest, notificationDtoList, notifications.hasNext());
	}

	private NotificationDto getNotificationDto(final Member member, final Notification notification) {
		Member sender = notification.getSender();
		MemberDtoForNotification senderDto = sender == null ? null : MemberDtoForNotification.from(sender,
			fileClient.getSignedUrl(sender.getProfileKey()));
		String message = notification.getType().getMessageWithParameter(getParameter(member.getId(), notification));
		//TODO jjh 변수로 관리하도록 수정(entity or service layer)
		String link = notification.getType().getLink();

		return NotificationDto.from(notification, message, link, senderDto);
	}

	private String getParameter(final Long memberId, final Notification notification) {
		String data = notification.getData();

		return switch (notification.getType()) {
			case FAMILY_REQUEST, FAMILY_REQUEST_COMPLETE -> notification.getSender().getNickname();
			case LEVEL_UP, LEVEL_DOWN -> data;
			case STAMP_REQUEST, STAMP_BOARD_COMPLETE, CREATED_STAMP_BOARD, ISSUED_COUPON -> {
				StampBoard stampBoard = stampBoardService.getStampBoard(Long.parseLong(data));
				yield stampBoard.getName();
			}
			case REWARD_REQUEST, REWARDED, REWARD_REQUEST_AGAIN, REWARD_FAIL, REWARDED_REQUEST -> {
				CouponDto coupon = couponService.getCoupon(memberId, Long.parseLong(data));
				yield coupon.reward();
			}
		};
	}
}
