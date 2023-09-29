package com.polzzak.domain.notification.entity;

import com.polzzak.domain.model.BaseEntity;
import com.polzzak.domain.notification.dto.UpdateNotificationSetting;
import com.polzzak.domain.user.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "notification_setting")
public class NotificationSetting extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean familyRequest;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean level;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean stampRequest;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean stampBoardComplete;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean rewardRequest;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean rewarded;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean rewardFail;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean createdStampBoard;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean issuedCoupon;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean rewardedRequest;

	@Builder(builderMethodName = "createNotificationSetting")
	public NotificationSetting(final Member member) {
		this.member = member;
		this.familyRequest = true;
		this.level = true;
		this.stampRequest = true;
		this.stampBoardComplete = true;
		this.rewardRequest = true;
		this.rewarded = true;
		this.rewardFail = true;
		this.createdStampBoard = true;
		this.issuedCoupon = true;
		this.rewardedRequest = true;
	}

	public void updateNotificationSetting(final UpdateNotificationSetting updateNotificationSetting) {
		if ( updateNotificationSetting.familyRequest() != null) {
			this.familyRequest = updateNotificationSetting.familyRequest();
		}
		if ( updateNotificationSetting.level() != null) {
			this.level = updateNotificationSetting.level();
		}
		if ( updateNotificationSetting.stampRequest() != null) {
			this.stampRequest = updateNotificationSetting.stampRequest();
		}
		if ( updateNotificationSetting.stampBoardComplete() != null) {
			this.stampBoardComplete = updateNotificationSetting.stampBoardComplete();
		}
		if ( updateNotificationSetting.rewardRequest() != null) {
			this.rewardRequest = updateNotificationSetting.rewardRequest();
		}
		if ( updateNotificationSetting.rewarded() != null) {
			this.rewarded = updateNotificationSetting.rewarded();
		}
		if ( updateNotificationSetting.rewardFail() != null) {
			this.rewardFail = updateNotificationSetting.rewardFail();
		}
		if ( updateNotificationSetting.createdStampBoard() != null) {
			this.createdStampBoard = updateNotificationSetting.createdStampBoard();
		}
		if ( updateNotificationSetting.issuedCoupon() != null) {
			this.issuedCoupon = updateNotificationSetting.issuedCoupon();
		}
		if ( updateNotificationSetting.rewardedRequest() != null) {
			this.rewardedRequest = updateNotificationSetting.rewardedRequest();
		}
	}
}
