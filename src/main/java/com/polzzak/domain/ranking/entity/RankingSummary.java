package com.polzzak.domain.ranking.entity;

import com.polzzak.domain.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingSummary extends BaseEntity {
	@Column(nullable = false)
	private int ranking;

	@Column(nullable = false, length = 5)
	@Enumerated(EnumType.STRING)
	private RankingStatus rankingStatus;

	@Column(nullable = false, length = 10)
	private String nickname;

	@Column(nullable = false)
	private int point;

	@Column(nullable = false)
	private int level;

	public RankingSummary(int ranking, RankingStatus rankingStatus, String nickname, int point, int level) {
		this.ranking = ranking;
		this.rankingStatus = rankingStatus;
		this.nickname = nickname;
		this.point = point;
		this.level = level;
	}
}
