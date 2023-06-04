package com.polzzak.domain.membertype.entity;

import com.polzzak.domain.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberTypeDetail extends BaseEntity {
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private MemberType type;

	@Column(nullable = false, length = 30, unique = true)
	private String detail;

	@Builder(builderMethodName = "createMemberType")
	public MemberTypeDetail(final MemberType memberType, final String detail) {
		this.type = memberType;
		this.detail = detail;
	}

	public boolean isKidType() {
		return type == MemberType.KID;
	}

	public boolean isGuardianType() {
		return type == MemberType.GUARDIAN;
	}

	public void update(final MemberType memberType, final String detail) {
		this.type = memberType;
		this.detail = detail;
	}
}
