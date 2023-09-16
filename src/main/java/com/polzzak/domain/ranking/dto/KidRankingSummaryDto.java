package com.polzzak.domain.ranking.dto;

import com.polzzak.domain.ranking.entity.KidRankingSummary;
import com.polzzak.domain.ranking.entity.RankingStatus;

public record KidRankingSummaryDto(
	int ranking,
	RankingStatus rankingStatus,
	String nickname,
	int point,
	int level,
	String profileUrl,
	boolean isMe
) {
	public static KidRankingSummaryDto of(final KidRankingSummary kidRankingSummary, final String profileUrl,
		final boolean isMe) {
		return new KidRankingSummaryDto(
			kidRankingSummary.getRanking(),
			kidRankingSummary.getRankingStatus(),
			kidRankingSummary.getNickname(),
			kidRankingSummary.getPoint(),
			kidRankingSummary.getLevel(),
			profileUrl,
			isMe
		);
	}
}
