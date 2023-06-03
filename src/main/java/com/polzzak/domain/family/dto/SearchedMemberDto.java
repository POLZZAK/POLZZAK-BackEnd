package com.polzzak.domain.family.dto;

import com.polzzak.domain.user.entity.Member;

public record SearchedMemberDto(
	Long memberId,
	String nickname,
	FamilyMemberTypeDto memberType,
	String profileUrl,
	FamilyStatus familyStatus
) {
	public static SearchedMemberDto from(final Member member, final String profileUrl,
		final FamilyStatus familyStatus) {
		return new SearchedMemberDto(member.getId(), member.getNickname(),
			FamilyMemberTypeDto.from(member.getMemberType()), profileUrl, familyStatus);
	}
}
