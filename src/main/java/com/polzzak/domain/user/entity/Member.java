package com.polzzak.domain.user.entity;

import com.polzzak.domain.membertype.entity.MemberTypeDetail;
import com.polzzak.domain.model.BaseModifiableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseModifiableEntity {

	@Column(nullable = false, length = 10, unique = true)
	private String nickname;

	@Column(nullable = false)
	private String profileKey;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_type_detail_id", nullable = false)
	private MemberTypeDetail memberType;

	@Builder(builderMethodName = "createMember")
	public Member(final String nickname, final String profileKey, final MemberTypeDetail memberType) {
		this.nickname = nickname;
		this.profileKey = profileKey;
		this.memberType = memberType;
	}

	public boolean isKid() {
		return memberType.isKidType();
	}

	public boolean isGuardian() {
		return memberType.isGuardianType();
	}

	public void changeProfileKey(final String profileKey) {
		this.profileKey = profileKey;
	}
}
