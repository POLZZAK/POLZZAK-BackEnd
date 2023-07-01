package com.polzzak.domain.memberpoint.dto;

import com.polzzak.domain.memberpoint.entity.MemberPoint;

public record MemberPointResponse(
	int point,
	int level
) {
	public static MemberPointResponse from(final MemberPoint memberPoint) {
		return new MemberPointResponse(memberPoint.getPoint(), memberPoint.getLevel());
	}
}
