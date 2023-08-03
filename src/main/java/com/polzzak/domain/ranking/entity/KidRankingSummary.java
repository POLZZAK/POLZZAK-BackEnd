package com.polzzak.domain.ranking.entity;

import java.time.LocalDateTime;

import com.polzzak.domain.memberpoint.entity.MemberPoint;

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
	public KidRankingSummary(final int ranking, final RankingStatus rankingStatus, final String nickname,
		final int point, final int level, final String profileKey, final LocalDateTime createdDate,
		final MemberPoint memberPoint) {
		super(ranking, rankingStatus, nickname, point, level, profileKey, createdDate, memberPoint);
	}
}
