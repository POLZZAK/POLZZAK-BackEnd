package com.polzzak.domain.ranking.service;

import lombok.Getter;

@Getter
public class RankingPosition {
	private int ranking = 0;
	private int prevPoint = Integer.MAX_VALUE;

	public void updateRankingPosition(final int point) {
		if (this.prevPoint > point) {
			this.ranking += 1;
			this.prevPoint = point;
		}
	}
}
