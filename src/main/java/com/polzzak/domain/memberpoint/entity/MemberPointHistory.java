package com.polzzak.domain.memberpoint.entity;

import com.polzzak.domain.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPointHistory extends BaseEntity {
	@Column(nullable = false, updatable = false, length = 30)
	private String description;

	@Column(nullable = false, updatable = false)
	private int increasedPoint;

	@Column(nullable = false, updatable = false)
	private int remainingPoint;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false, updatable = false)
	private MemberPoint memberPoint;

	@Builder(builderMethodName = "createMemberPointHistory")
	public MemberPointHistory(final String description, final int increasedPoint, final int remainingPoint,
		final MemberPoint memberPoint) {
		this.description = description;
		this.increasedPoint = increasedPoint;
		this.remainingPoint = remainingPoint;
		this.memberPoint = memberPoint;
	}
}
