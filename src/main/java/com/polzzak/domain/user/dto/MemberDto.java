package com.polzzak.domain.user.dto;

import com.polzzak.domain.user.entity.Member;

public record MemberDto(
	Long memberId,
	String nickname,
	MemberTypeDto memberType
) {
	public static MemberDto from(final Member member) {
		return new MemberDto(member.getId(), member.getNickname(), MemberTypeDto.from(member.getMemberType()));
	}
}
