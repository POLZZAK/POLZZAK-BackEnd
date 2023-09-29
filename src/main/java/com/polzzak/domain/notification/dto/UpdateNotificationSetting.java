package com.polzzak.domain.notification.dto;

import jakarta.annotation.Nullable;

public record UpdateNotificationSetting(
	@Nullable Boolean familyRequest, @Nullable Boolean level, @Nullable Boolean stampRequest,
	@Nullable Boolean stampBoardComplete, @Nullable Boolean rewardRequest, @Nullable Boolean rewarded,
	@Nullable Boolean rewardFail, @Nullable Boolean createdStampBoard, @Nullable Boolean issuedCoupon,
	@Nullable Boolean rewardedRequest
) {
}
