package com.polzzak.domain.family.dto;

import java.util.List;

public record SearchedMemberListResponse(
	List<SearchedMemberDto> members
) {
	public static SearchedMemberListResponse from(List<SearchedMemberDto> families) {
		return new SearchedMemberListResponse(families);
	}
}
