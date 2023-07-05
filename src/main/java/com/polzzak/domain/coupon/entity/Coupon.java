package com.polzzak.domain.coupon.entity;

import java.time.LocalDateTime;

import com.polzzak.domain.model.BaseEntity;
import com.polzzak.domain.stampboard.entity.StampBoard;
import com.polzzak.domain.user.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "coupon")
public class Coupon extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guardian_id")
	private Member guardian;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kid_id")
	private Member kid;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stamp_board_id")
	private StampBoard stampBoard;

	@Column(nullable = false)
	private String reward;

	@Column(nullable = false)
	private LocalDateTime rewardDate;

	public boolean isNotOwner(long kidId) {
		return kid.getId() != kidId;
	}

	@Builder(builderMethodName = "createCoupon")
	public Coupon(Member guardian, Member kid, StampBoard stampBoard, String reward, LocalDateTime rewardDate) {
		this.guardian = guardian;
		this.kid = kid;
		this.stampBoard = stampBoard;
		this.reward = reward;
		this.rewardDate = rewardDate;
	}
}
