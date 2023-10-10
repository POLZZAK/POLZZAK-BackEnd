package com.polzzak.domain.pushtoken.model;

import com.polzzak.domain.model.BaseEntity;
import com.polzzak.domain.user.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "push_token", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"member_id", "token"})
})
public class PushToken extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(nullable = false)
	private String token;

	@Builder(builderMethodName = "createPushToken")
	public PushToken(Member member, String token) {
		this.member = member;
		this.token = token;
	}
}
