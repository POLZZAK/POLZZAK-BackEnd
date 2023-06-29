package com.polzzak.domain.user.dto;

import com.polzzak.domain.memberpoint.entity.MemberPoint;
import com.polzzak.domain.user.entity.Member;

public record MemberResponse(
	Long memberId,
	String nickname,
	MemberPointDto memberPoint,
	MemberTypeDto memberType,
	String profileUrl,
	int familyCount
) {
	public static MemberResponse from(final Member member, final MemberPoint memberPoint, final String profileUrl,
		final int familyCount) {
		return new MemberResponse(member.getId(), member.getNickname(), MemberPointDto.from(memberPoint),
			MemberTypeDto.from(member.getMemberType()), profileUrl, familyCount);
	}
}
