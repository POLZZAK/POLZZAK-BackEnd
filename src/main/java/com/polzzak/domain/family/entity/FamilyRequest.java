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
public class FamilyRequest extends BaseModifiableEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id", nullable = false, updatable = false)
	private Member sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id", nullable = false, updatable = false)
	private Member receiver;

	@Builder(builderMethodName = "createFamilyRequest")
	public FamilyRequest(final Member sender, final Member receiver) {
		this.sender = sender;
		this.receiver = receiver;
	}
}
