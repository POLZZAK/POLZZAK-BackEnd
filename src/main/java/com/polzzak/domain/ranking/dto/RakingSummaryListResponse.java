package com.polzzak.domain.ranking.dto;

import java.util.List;

import com.polzzak.domain.user.dto.MemberSimpleResponse;

public record RakingSummaryListResponse<T>(
	MemberSimpleResponse memberSimpleResponse,
	List<T> rankingSummaries
) {
}
