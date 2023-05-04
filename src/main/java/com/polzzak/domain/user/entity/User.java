package com.polzzak.domain.user.entity;

import java.time.LocalDateTime;

import com.polzzak.domain.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users", indexes = @Index(name = "idx_username", columnList = "username"))
public class User extends BaseEntity {
	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SocialType socialType;

	@Column(nullable = false)
	private boolean withdraw;

	@Column(nullable = false)
	private LocalDateTime signedDate;

	@JoinColumn(name = "member_id")
	@OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE,
		CascadeType.REMOVE}, orphanRemoval = true)
	private Member member;

	@Builder(builderMethodName = "createUser")
	public User(final String username, final SocialType socialType, final Member member) {
		this.username = username;
		this.socialType = socialType;
		this.member = member;
		this.withdraw = false;
		this.signedDate = LocalDateTime.now();
	}

	public void withdraw() {
		this.withdraw = true;
	}
}
