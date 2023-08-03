package com.polzzak.support;

import java.util.List;

import com.polzzak.domain.ranking.dto.GuardianRankingSummaryDto;
import com.polzzak.domain.ranking.dto.KidRankingSummaryDto;
import com.polzzak.domain.ranking.dto.RakingSummaryListResponse;
import com.polzzak.domain.ranking.entity.RankingStatus;

public class RankingFixtures {
	public static final RakingSummaryListResponse GUARDIAN_RANKING_SUMMARY_LIST_RESPONSE;
	public static final RakingSummaryListResponse KID_RANKING_SUMMARY_LIST_RESPONSE;

	static {
		GUARDIAN_RANKING_SUMMARY_LIST_RESPONSE = new RakingSummaryListResponse(List.of(
			new GuardianRankingSummaryDto(1, RankingStatus.UP, "guardianNickname1", 300, 3, "삼촌", "profileUrl1"),
			new GuardianRankingSummaryDto(2, RankingStatus.UP, "guardianNickname2", 200, 2, "엄마", "profileUrl2"))
		);
		KID_RANKING_SUMMARY_LIST_RESPONSE = new RakingSummaryListResponse(List.of(
			new KidRankingSummaryDto(1, RankingStatus.UP, "kidNickname1", 300, 3, "profileUrl1"),
			new KidRankingSummaryDto(2, RankingStatus.UP, "kidNickname2", 200, 2, "profileUrl2"))
		);
	}
}
