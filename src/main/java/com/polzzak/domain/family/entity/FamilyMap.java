package com.polzzak.domain.family.entity;

import com.polzzak.domain.model.BaseModifiableEntity;
import com.polzzak.domain.user.entity.Member;

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
public class FamilyMap extends BaseModifiableEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guardian_id", nullable = false, updatable = false)
	private Member guardian;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kid_id", nullable = false, updatable = false)
	private Member kid;

	@Builder(builderMethodName = "createFamilyMap")
	public FamilyMap(final Member guardian, final Member kid) {
		this.guardian = guardian;
		this.kid = kid;
	}
}
