package com.polzzak.domain.ranking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuardianRankingSummary extends RankingSummary {
	@Column(nullable = false, length = 30)
	private String memberTypeDetail;

	@Builder(builderMethodName = "createGuardianRankingSummary")
	public GuardianRankingSummary(int ranking, RankingStatus rankingStatus, String nickname, int point, int level,
		String memberTypeDetail) {
		super(ranking, rankingStatus, nickname, point, level);
		this.memberTypeDetail = memberTypeDetail;
	}
}
