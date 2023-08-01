package com.polzzak.domain.ranking.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RankingStatus {
	UP("상승"), DOWN("하락"), HOLD("유지");

	private final String description;
}
