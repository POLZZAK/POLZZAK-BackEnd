package com.polzzak.domain.ranking.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.polzzak.domain.memberpoint.entity.MemberPoint;
import com.polzzak.domain.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
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

	@Column(nullable = false)
	private String profileKey;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdDate;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_point_id", nullable = false, updatable = false)
	private MemberPoint memberPoint;

	public RankingSummary(final int ranking, final RankingStatus rankingStatus, final String nickname, final int point,
		final int level, final String profileKey, final LocalDateTime createdDate, final MemberPoint memberPoint) {
		this.ranking = ranking;
		this.rankingStatus = rankingStatus;
		this.nickname = nickname;
		this.point = point;
		this.level = level;
		this.profileKey = profileKey;
		this.createdDate = createdDate;
		this.memberPoint = memberPoint;
	}
}
