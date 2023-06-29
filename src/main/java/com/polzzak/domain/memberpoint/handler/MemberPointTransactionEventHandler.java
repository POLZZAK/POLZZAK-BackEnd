package com.polzzak.domain.memberpoint.handler;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.polzzak.domain.memberpoint.entity.MemberPoint;
import com.polzzak.domain.memberpoint.entity.MemberPointEvent;
import com.polzzak.domain.memberpoint.entity.MemberPointHistory;
import com.polzzak.domain.memberpoint.entity.MemberPointType;
import com.polzzak.domain.memberpoint.service.MemberPointService;

@Component
public class MemberPointTransactionEventHandler {
	private final MemberPointService memberPointService;

	public MemberPointTransactionEventHandler(final MemberPointService memberPointService) {
		this.memberPointService = memberPointService;
	}

	@Async
	@TransactionalEventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleMemberPointEvent(final MemberPointEvent event) {
		event.members()
			.forEach(member -> {
				MemberPoint memberPoint = memberPointService.getMemberPoint(member.getId());
				memberPoint.updatePoint(event.memberPointType().getIncreasedPoint());
				memberPointService.saveMemberPointHistory(
					createMemberPointHistory(event.memberPointType(), memberPoint));
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
