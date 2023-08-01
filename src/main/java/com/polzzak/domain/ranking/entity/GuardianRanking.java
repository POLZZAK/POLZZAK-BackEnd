package com.polzzak.domain.ranking.entity;

import com.polzzak.domain.memberpoint.entity.MemberPoint;
import com.polzzak.domain.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuardianRanking extends BaseEntity {
	@Column(nullable = false)
	private int ranking;

	@Column(nullable = false, length = 5)
	@Enumerated(EnumType.STRING)
	private RankingStatus rankingStatus;

	@OneToOne
	@JoinColumn(name = "member_point_id", nullable = false, updatable = false)
	private MemberPoint guardianPoint;

	@Builder(builderMethodName = "createGuardianRanking")
	public GuardianRanking(final int ranking, final RankingStatus rankingStatus, final MemberPoint guardianPoint) {
		this.ranking = ranking;
		this.rankingStatus = rankingStatus;
		this.guardianPoint = guardianPoint;
	}
}
