package com.polzzak.domain.family.dto;

import java.util.List;

public record FamilyMemberListResponse(
	List<FamilyMemberDto> families
) {
	public static FamilyMemberListResponse from(List<FamilyMemberDto> families) {
		return new FamilyMemberListResponse(families);
	}
}
