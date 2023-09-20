package com.polzzak.domain.notification.dto;

import com.polzzak.domain.notification.entity.NotificationSetting;
import com.polzzak.domain.user.entity.Member;

public record NotificationSettingDto(
	Boolean familyRequest, Boolean level, Boolean stampRequest, Boolean stampBoardComplete, Boolean rewardRequest,
	Boolean rewarded, Boolean rewardFail, Boolean createdStampBoard, Boolean issuedCoupon
) {

	public static NotificationSettingDto from(final NotificationSetting notificationSetting) {
		Member member = notificationSetting.getMember();

		if (member.isKid()) {
			return new NotificationSettingDto(notificationSetting.isFamilyRequest(), notificationSetting.isLevel(),
				null, null, null, notificationSetting.isRewarded(), null, notificationSetting.isCreatedStampBoard(),
				notificationSetting.isIssuedCoupon());
		}
		return new NotificationSettingDto(notificationSetting.isFamilyRequest(), notificationSetting.isLevel(),
			notificationSetting.isStampRequest(), notificationSetting.isStampBoardComplete(),
			notificationSetting.isRewardRequest(), notificationSetting.isRewarded(),
			notificationSetting.isRewardFail(), null, null);
	}
}
