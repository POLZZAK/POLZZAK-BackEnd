package com.polzzak.domain.user.dto;

import com.polzzak.domain.memberpoint.entity.MemberPoint;
import com.polzzak.domain.user.entity.Member;

public record MemberSimpleResponse(
	Long memberId,
	String nickname,
	MemberPointDto memberPoint,
	MemberTypeDto memberType,
	String profileUrl,
	int myRanking
) {
	public static MemberSimpleResponse from(final Member member, final MemberPoint memberPoint,
		final String profileUrl, final int myRanking) {
		return new MemberSimpleResponse(member.getId(), member.getNickname(), MemberPointDto.from(memberPoint),
			MemberTypeDto.from(member.getMemberType()), profileUrl, myRanking);
	}
}
