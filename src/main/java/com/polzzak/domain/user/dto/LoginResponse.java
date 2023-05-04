package com.polzzak.domain.user.dto;

import com.polzzak.domain.user.entity.SocialType;

public record LoginResponse(
	String username,
	SocialType socialType
) {
}
