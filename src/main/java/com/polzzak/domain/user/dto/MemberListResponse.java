package com.polzzak.domain.user.dto;

import java.util.List;

public record MemberListResponse(
	List<MemberDto> members
) {
	public static MemberListResponse from(final List<MemberDto> members) {
		return new MemberListResponse(members);
	}
}
