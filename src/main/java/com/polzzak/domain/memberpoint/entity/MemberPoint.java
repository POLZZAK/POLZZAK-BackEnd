package com.polzzak.domain.memberpoint.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.polzzak.domain.user.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class MemberPoint {
	@Id
	@Column(updatable = false)
	private Long memberId;

	@MapsId
	@OneToOne(fetch = FetchType.LAZY)
	private Member member;

	@Column(nullable = false)
	private int point = 50;

	@Column(nullable = false)
	private int level = 0;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdDate;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime lastModifiedDate;

	@Builder(builderMethodName = "createMemberPoint")
	public MemberPoint(final Member member) {
		this.memberId = member.getId();
		this.member = member;
	}

	public void updatePoint(final int point) {
		this.point += point;
		if (point < 0) {
			this.point = 0;
		}
		this.level = this.point / 100;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		final MemberPoint that = (MemberPoint)obj;
		return Objects.equals(member.getId(), that.getMember().getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(member.getId());
	}
}
