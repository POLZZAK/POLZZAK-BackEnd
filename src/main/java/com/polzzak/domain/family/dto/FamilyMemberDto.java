package com.polzzak.domain.family.dto;

import com.polzzak.domain.user.entity.Member;

public record FamilyMemberDto(
	Long memberId,
	String nickname,
	FamilyMemberTypeDto memberType,
	String profileUrl
) {
	public static FamilyMemberDto from(final Member member, final String profileUrl) {
		return new FamilyMemberDto(member.getId(), member.getNickname(),
			FamilyMemberTypeDto.from(member.getMemberType()), profileUrl);
	}
}
