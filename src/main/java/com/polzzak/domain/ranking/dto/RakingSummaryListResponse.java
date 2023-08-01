package com.polzzak.domain.ranking.dto;

import java.util.List;

public record RakingSummaryListResponse<T>(
	List<T> rankingSummaryList
) {
}
