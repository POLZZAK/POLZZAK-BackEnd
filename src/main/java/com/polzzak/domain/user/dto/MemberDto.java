package com.polzzak.domain.user.dto;

import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.entity.MemberType;

public record MemberDto(
	Long memberId,
	String nickname,
	MemberType memberType,
	String profileUrl
) {
	public static MemberDto from(final Member member, final String profileUrl) {
		return new MemberDto(member.getId(), member.getNickname(), member.getMemberType(), profileUrl);
	}
}
