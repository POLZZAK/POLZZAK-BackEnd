package com.polzzak.domain.ranking.entity;

import java.time.LocalDateTime;

import com.polzzak.domain.memberpoint.entity.MemberPoint;

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
	public GuardianRankingSummary(final int ranking, final RankingStatus rankingStatus, final String nickname,
		final int point, final int level, final String profileKey, final LocalDateTime createdDate,
		final MemberPoint memberPoint, final String memberTypeDetail) {
		super(ranking, rankingStatus, nickname, point, level, profileKey, createdDate, memberPoint);
		this.memberTypeDetail = memberTypeDetail;
	}
}
