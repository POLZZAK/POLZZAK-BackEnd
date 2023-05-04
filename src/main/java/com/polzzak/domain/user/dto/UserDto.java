package com.polzzak.domain.user.dto;

import com.polzzak.domain.user.entity.MemberType;
import com.polzzak.domain.user.entity.SocialType;
import com.polzzak.domain.user.entity.User;

public record UserDto(
	String username,
	String nickname,
	MemberType memberType,
	SocialType socialType,
	String profileUrl
) {
	public static UserDto from(final User user, final String profileUrl) {
		return new UserDto(user.getUsername(), user.getMember().getNickname(), user.getMember().getMemberType(),
			user.getSocialType(), profileUrl);
	}
}
