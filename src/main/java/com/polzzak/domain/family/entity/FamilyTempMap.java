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
public class FamilyTempMap extends BaseEntity {
	@Column(nullable = false, updatable = false)
	private Long senderId;

	@Column(nullable = false, updatable = false)
	private Long receiverId;

	@Builder(builderMethodName = "createFamilyTempMap")
	public FamilyTempMap(final Long senderId, final Long receiverId) {
		this.senderId = senderId;
		this.receiverId = receiverId;
	}
}
