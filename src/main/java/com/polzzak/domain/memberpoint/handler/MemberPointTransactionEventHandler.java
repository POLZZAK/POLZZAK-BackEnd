package com.polzzak.domain.memberpoint.handler;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.polzzak.domain.family.entity.FamilyMapCreatedEvent;
import com.polzzak.domain.memberpoint.entity.MemberPoint;
import com.polzzak.domain.memberpoint.entity.MemberPointHistory;
import com.polzzak.domain.memberpoint.entity.MemberPointType;
import com.polzzak.domain.memberpoint.service.MemberPointService;
import com.polzzak.domain.stampboard.entity.StampBoardCreatedEvent;
import com.polzzak.domain.stampboard.entity.StampBoardDeletedEvent;
import com.polzzak.domain.stampboard.entity.StampCreatedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MemberPointTransactionEventHandler {
	private final MemberPointService memberPointService;

	public MemberPointTransactionEventHandler(final MemberPointService memberPointService) {
		this.memberPointService = memberPointService;
	}

	@Async
	@TransactionalEventListener
	public void handleStampBoardCreatedEvent(final StampBoardCreatedEvent event) {
		try {
			MemberPoint memberPoint = memberPointService.getMemberPointWithWriteLock(event.guardian().getId());
			memberPoint.updatePoint(MemberPointType.STAMP_BOARD_CREATION.getIncreasedPoint());
			memberPointService.saveMemberPointHistory(
				createMemberPointHistory(MemberPointType.STAMP_BOARD_CREATION, memberPoint));
		} catch (RuntimeException ex) {
			log.error("[handleStampBoardCreatedEvent]", ex);
		}
	}

	@Async
	@TransactionalEventListener
	public void handleStampCreatedEvent(final StampCreatedEvent event) {
		try {
			event.members()
				.forEach(member -> {
					MemberPoint memberPoint = memberPointService.getMemberPointWithWriteLock(member.getId());
					memberPoint.updatePoint(MemberPointType.STAMP_CREATION.getIncreasedPoint());
					memberPointService.saveMemberPointHistory(
						createMemberPointHistory(MemberPointType.STAMP_CREATION, memberPoint));
				});
		} catch (RuntimeException ex) {
			log.error("[handleStampCreatedEvent]", ex);
		}
	}

	@Async
	@TransactionalEventListener
	public void handleStampBoardDeletedEvent(final StampBoardDeletedEvent event) {
		try {
			MemberPoint memberPoint = memberPointService.getMemberPointWithWriteLock(event.guardian().getId());
			memberPoint.updatePoint(MemberPointType.STAMP_BOARD_REMOVAL.getIncreasedPoint());
			memberPointService.saveMemberPointHistory(
				createMemberPointHistory(MemberPointType.STAMP_BOARD_REMOVAL, memberPoint));
		} catch (RuntimeException ex) {
			log.error("[handleStampBoardDeletedEvent]", ex);
		}
	}

	@Async
	@TransactionalEventListener
	public void handleFamilyMapCreatedEvent(final FamilyMapCreatedEvent event) {
		try {
			event.members()
				.forEach(member -> {
					MemberPoint memberPoint = memberPointService.getMemberPointWithWriteLock(member.getId());
					memberPoint.updatePoint(MemberPointType.FAMILY_MAP_CREATION.getIncreasedPoint());
					memberPointService.saveMemberPointHistory(
						createMemberPointHistory(MemberPointType.FAMILY_MAP_CREATION, memberPoint));
				});
		} catch (RuntimeException ex) {
			log.error("[handleFamilyMapCreatedEvent]", ex);
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
