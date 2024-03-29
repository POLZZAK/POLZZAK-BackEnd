package com.polzzak.domain.coupon.entity;

import java.time.LocalDateTime;

import com.polzzak.domain.model.BaseEntity;
import com.polzzak.domain.stampboard.entity.StampBoard;
import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.domain.user.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

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
	@Enumerated(EnumType.STRING)
	private CouponState state;

	@Column(nullable = false)
	private LocalDateTime requestDate;

	@Column(nullable = false)
	private LocalDateTime rewardDate;

	@Builder(builderMethodName = "createCoupon")
	public Coupon(final Member guardian, final Member kid, final StampBoard stampBoard, final String reward,
		final LocalDateTime rewardDate) {
		this.guardian = guardian;
		this.kid = kid;
		this.stampBoard = stampBoard;
		this.reward = reward;
		this.state = CouponState.ISSUED;
		this.rewardDate = rewardDate;
	}

	public boolean isNotOwner(final MemberDto member) {
		return guardian.getId() != member.memberId() && kid.getId() != member.memberId();
	}

	public boolean isNotOwner(final Member member) {
		return guardian.getId() != member.getId() && kid.getId() != member.getId();
	}

	public void receiveReward() {
		this.state = CouponState.REWARDED;
	}

	public void requestReward() {
		requestDate = LocalDateTime.now();
	}

	public boolean isPossibleRequest() {
		if (requestDate == null) {
			return true;
		}
		return requestDate.isBefore(LocalDateTime.now().minusHours(1));
	}

	@RequiredArgsConstructor
	public enum CouponState {
		ISSUED("쿠폰 발급"), REWARDED("선물 수령");

		private final String description;
	}

}
