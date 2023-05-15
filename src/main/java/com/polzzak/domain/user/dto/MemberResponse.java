package com.polzzak.domain.user.dto;

import com.polzzak.domain.user.entity.MemberType;

public record MemberResponse(
	String nickname,
	MemberType memberType,
	String profileUrl
) {
	public static MemberResponse from(final MemberDto memberDto) {
		return new MemberResponse(memberDto.nickname(), memberDto.memberType(), memberDto.profileUrl());
	}
}
