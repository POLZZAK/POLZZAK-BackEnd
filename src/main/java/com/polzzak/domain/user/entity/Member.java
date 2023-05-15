package com.polzzak.domain.user.entity;

import com.polzzak.domain.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(indexes = @Index(name = "idx_nickname", columnList = "nickname"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private MemberType memberType;

	@Column(nullable = false, length = 10, unique = true)
	private String nickname;

	@Column(nullable = false)
	private String profileKey;

	@Builder(builderMethodName = "createMember")
	public Member(final MemberType memberType, final String nickname, final String profileKey) {
		this.memberType = memberType;
		this.nickname = nickname;
		this.profileKey = profileKey;
	}
}
