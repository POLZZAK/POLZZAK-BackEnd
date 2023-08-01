package com.polzzak.domain.ranking.entity;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KidRankingSummary extends RankingSummary {
	@Builder(builderMethodName = "createKidRankingSummary")
	public KidRankingSummary(int ranking, RankingStatus rankingStatus, String nickname, int point, int level) {
		super(ranking, rankingStatus, nickname, point, level);
	}
}
