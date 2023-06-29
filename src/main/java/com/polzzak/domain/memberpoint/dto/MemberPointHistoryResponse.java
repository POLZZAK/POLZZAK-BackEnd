package com.polzzak.domain.memberpoint.dto;

import java.time.LocalDateTime;

import com.polzzak.domain.memberpoint.entity.MemberPointHistory;

public record MemberPointHistoryResponse(
	String description,
	int increasedPoint,
	int remainingPoint,
	LocalDateTime createdDate
) {
	public static MemberPointHistoryResponse from(final MemberPointHistory memberPointHistory) {
		return new MemberPointHistoryResponse(
			memberPointHistory.getDescription(),
			memberPointHistory.getIncreasedPoint(),
			memberPointHistory.getRemainingPoint(),
			memberPointHistory.getCreatedDate()
		);
	}
}
