package com.polzzak.domain.user.dto;

import com.polzzak.domain.memberpoint.entity.MemberPoint;

public record MemberPointDto(
	int point,
	int level
) {
	public static MemberPointDto from(final MemberPoint memberPoint) {
		return new MemberPointDto(memberPoint.getPoint(), memberPoint.getLevel());
	}
}
