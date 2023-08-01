package com.polzzak.domain.ranking.dto;

import com.polzzak.domain.ranking.entity.GuardianRankingSummary;
import com.polzzak.domain.ranking.entity.RankingStatus;

public record GuardianRankingSummaryDto(
	int ranking,
	RankingStatus rankingStatus,
	String nickname,
	int point,
	int level,
	String memberTypeDetail,
	String profileUrl
) {
	public static GuardianRankingSummaryDto of(final GuardianRankingSummary guardianRankingSummary,
		final String profileUrl) {
		return new GuardianRankingSummaryDto(
			guardianRankingSummary.getRanking(),
			guardianRankingSummary.getRankingStatus(),
			guardianRankingSummary.getNickname(),
			guardianRankingSummary.getPoint(),
			guardianRankingSummary.getLevel(),
			guardianRankingSummary.getMemberTypeDetail(),
			profileUrl
		);
	}
}
