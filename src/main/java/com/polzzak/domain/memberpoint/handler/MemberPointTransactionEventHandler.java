package com.polzzak.domain.memberpoint.handler;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.polzzak.domain.family.entity.FamilyMapCreatedEvent;
import com.polzzak.domain.memberpoint.entity.MemberPoint;
import com.polzzak.domain.memberpoint.entity.MemberPointHistory;
import com.polzzak.domain.memberpoint.entity.MemberPointType;
import com.polzzak.domain.memberpoint.service.MemberPointService;
import com.polzzak.domain.stampboard.entity.StampBoardCreatedEvent;
import com.polzzak.domain.stampboard.entity.StampBoardDeletedEvent;
import com.polzzak.domain.stampboard.entity.StampCreatedEvent;

@Component
public class MemberPointTransactionEventHandler {
	private final MemberPointService memberPointService;

	public MemberPointTransactionEventHandler(final MemberPointService memberPointService) {
		this.memberPointService = memberPointService;
	}

	@Async
	@TransactionalEventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleStampBoardCreateEvent(final StampBoardCreatedEvent event) {
		MemberPoint memberPoint = memberPointService.getMemberPoint(event.guardian().getId());
		memberPoint.updatePoint(MemberPointType.STAMP_BOARD_CREATION.getIncreasedPoint());
		memberPointService.saveMemberPointHistory(
			createMemberPointHistory(MemberPointType.STAMP_BOARD_CREATION, memberPoint));
	}

	@Async
	@TransactionalEventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleStampCreateEvent(final StampCreatedEvent event) {
		event.members()
			.forEach(member -> {
				MemberPoint memberPoint = memberPointService.getMemberPoint(member.getId());
				memberPoint.updatePoint(MemberPointType.STAMP_CREATION.getIncreasedPoint());
				memberPointService.saveMemberPointHistory(
					createMemberPointHistory(MemberPointType.STAMP_CREATION, memberPoint));
			});
	}

	@Async
	@TransactionalEventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleStampBoardDeleteEvent(final StampBoardDeletedEvent event) {
		MemberPoint memberPoint = memberPointService.getMemberPoint(event.guardian().getId());
		memberPoint.updatePoint(MemberPointType.STAMP_BOARD_REMOVAL.getIncreasedPoint());
		memberPointService.saveMemberPointHistory(
			createMemberPointHistory(MemberPointType.STAMP_BOARD_REMOVAL, memberPoint));
	}

	@Async
	@TransactionalEventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleFamilyMapCreateEvent(final FamilyMapCreatedEvent event) {
		event.members()
			.forEach(member -> {
				MemberPoint memberPoint = memberPointService.getMemberPoint(member.getId());
				memberPoint.updatePoint(MemberPointType.FAMILY_MAP_CREATION.getIncreasedPoint());
				memberPointService.saveMemberPointHistory(
					createMemberPointHistory(MemberPointType.FAMILY_MAP_CREATION, memberPoint));
			});
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
