package com.polzzak.domain.family.dto;

import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.entity.MemberType;

public record FamilyMemberDto(
	Long memberId,
	String nickname,
	MemberType memberType,
	String profileUrl
) {
	public static FamilyMemberDto from(final Member member, final String profileUrl) {
		return new FamilyMemberDto(member.getId(), member.getNickname(), member.getMemberType(), profileUrl);
	}
}
