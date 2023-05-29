package com.polzzak.domain.stampboard.dto;

import java.util.List;

import com.polzzak.domain.family.dto.FamilyMemberDto;

public record FamilyStampBoardSummary(FamilyMemberDto partner, List<StampBoardSummary> stampBoardSummaries) {

	public static FamilyStampBoardSummary from(FamilyMemberDto partner, List<StampBoardSummary> stampBoardSummaries) {
		return new FamilyStampBoardSummary(partner, stampBoardSummaries);
	}
}
