package com.polzzak.domain.memberpoint.handler;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.polzzak.domain.family.entity.FamilyMapCreatedEvent;
import com.polzzak.domain.memberpoint.entity.MemberPoint;
import com.polzzak.domain.memberpoint.entity.MemberPointHistory;
import com.polzzak.domain.memberpoint.entity.MemberPointType;
import com.polzzak.domain.memberpoint.service.MemberPointService;
import com.polzzak.domain.notification.dto.NotificationCreateEvent;
import com.polzzak.domain.notification.entity.NotificationType;
import com.polzzak.domain.stampboard.entity.StampBoardCreatedEvent;
import com.polzzak.domain.stampboard.entity.StampBoardDeletedEvent;
import com.polzzak.domain.stampboard.entity.StampCreatedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MemberPointTransactionEventHandler {
	private final MemberPointService memberPointService;
	private final ApplicationEventPublisher eventPublisher;

	public MemberPointTransactionEventHandler(final MemberPointService memberPointService,
		ApplicationEventPublisher eventPublisher) {
		this.memberPointService = memberPointService;
		this.eventPublisher = eventPublisher;
	}

	@Async
	@Transactional
	@TransactionalEventListener
	public void handleStampBoardCreatedEvent(final StampBoardCreatedEvent event) {
		try {
			MemberPoint memberPoint = memberPointService.getMemberPointWithWriteLock(event.guardian().getId());
			int beforeMemberPoint = memberPoint.getPoint();
			memberPoint.updatePoint(MemberPointType.STAMP_BOARD_CREATION.getIncreasedPoint());
			int currentMemberPoint = memberPoint.getPoint();

			memberPointService.saveMemberPointHistory(
				createMemberPointHistory(MemberPointType.STAMP_BOARD_CREATION, memberPoint));

			calculateMemberLevel(memberPoint.getMemberId(), beforeMemberPoint, currentMemberPoint);
		} catch (RuntimeException ex) {
			log.error("[handleStampBoardCreatedEvent]", ex);
		}
	}

	@Async
	@Transactional
	@TransactionalEventListener
	public void handleStampCreatedEvent(final StampCreatedEvent event) {
		try {
			event.members()
				.forEach(member -> {
					MemberPoint memberPoint = memberPointService.getMemberPointWithWriteLock(member.getId());
					int beforeMemberPoint = memberPoint.getPoint();
					memberPoint.updatePoint(MemberPointType.STAMP_CREATION.getIncreasedPoint());
					int currentMemberPoint = memberPoint.getPoint();

					memberPointService.saveMemberPointHistory(
						createMemberPointHistory(MemberPointType.STAMP_CREATION, memberPoint));

					calculateMemberLevel(memberPoint.getMemberId(), beforeMemberPoint, currentMemberPoint);
				});
		} catch (RuntimeException ex) {
			log.error("[handleStampCreatedEvent]", ex);
		}
	}

	@Async
	@Transactional
	@TransactionalEventListener
	public void handleStampBoardDeletedEvent(final StampBoardDeletedEvent event) {
		try {
			MemberPoint memberPoint = memberPointService.getMemberPointWithWriteLock(event.guardian().getId());

			int beforeMemberPoint = memberPoint.getPoint();
			memberPoint.updatePoint(MemberPointType.STAMP_BOARD_REMOVAL.getIncreasedPoint());
			int currentMemberPoint = memberPoint.getPoint();
			memberPointService.saveMemberPointHistory(
				createMemberPointHistory(MemberPointType.STAMP_BOARD_REMOVAL, memberPoint));

			calculateMemberLevel(memberPoint.getMemberId(), beforeMemberPoint, currentMemberPoint);
		} catch (RuntimeException ex) {
			log.error("[handleStampBoardDeletedEvent]", ex);
		}
	}

	@Async
	@Transactional
	@TransactionalEventListener
	public void handleFamilyMapCreatedEvent(final FamilyMapCreatedEvent event) {
		try {
			event.members()
				.forEach(member -> {
					MemberPoint memberPoint = memberPointService.getMemberPointWithWriteLock(member.getId());
					int beforeMemberPoint = memberPoint.getPoint();
					memberPoint.updatePoint(MemberPointType.FAMILY_MAP_CREATION.getIncreasedPoint());
					int currentMemberPoint = memberPoint.getPoint();
					memberPointService.saveMemberPointHistory(
						createMemberPointHistory(MemberPointType.FAMILY_MAP_CREATION, memberPoint));

					calculateMemberLevel(memberPoint.getMemberId(), beforeMemberPoint, currentMemberPoint);
				});
		} catch (RuntimeException ex) {
			log.error("[handleFamilyMapCreatedEvent]", ex);
		}
	}

	private void calculateMemberLevel(final long memberId, final int beforePoint, final int currentPoint) {
		int beforeLevel = beforePoint / 100;
		int currentLevel = currentPoint / 100;
		int diff = currentLevel - beforeLevel;

		if (diff > 0) {
			eventPublisher.publishEvent(
				new NotificationCreateEvent(null, memberId, NotificationType.LEVEL_UP, diff + "계단"));
		} else if (diff < 0) {
			eventPublisher.publishEvent(
				new NotificationCreateEvent(null, memberId, NotificationType.LEVEL_DOWN, diff + "계단"));
		}
	}

	private MemberPointHistory createMemberPointHistory(final MemberPointType pointType,
		final MemberPoint memberPoint) {
		return MemberPointHistory.createMemberPointHistory()
			.description(pointType.getDescription())
			.increasedPoint(pointType.getIncreasedPoint())
			.remainingPoint(memberPoint.getPoint())
			.memberPoint(memberPoint)
			.build();
	}
}
