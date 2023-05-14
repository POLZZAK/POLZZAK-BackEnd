package com.polzzak.domain.family.entity;

import com.polzzak.domain.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FamilyMap extends BaseEntity {
	@Column(nullable = false, updatable = false)
	private Long guardianId;

	@Column(nullable = false, updatable = false)
	private Long kidId;

	@Builder(builderMethodName = "createFamilyMap")
	public FamilyMap(final Long guardianId, final Long kidId) {
		this.guardianId = guardianId;
		this.kidId = kidId;
	}
}
