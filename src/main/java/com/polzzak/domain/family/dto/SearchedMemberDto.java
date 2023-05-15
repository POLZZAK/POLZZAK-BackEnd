package com.polzzak.domain.family.dto;

import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.domain.user.entity.MemberType;

public record SearchedMemberDto(
	Long memberId,
	String nickname,
	MemberType memberType,
	String profileUrl,
	FamilyStatus familyStatus
) {
	public static SearchedMemberDto from(final MemberDto memberDto, final FamilyStatus familyStatus) {
		return new SearchedMemberDto(memberDto.memberId(), memberDto.nickname(), memberDto.memberType(),
			memberDto.profileUrl(), familyStatus);
	}
}
