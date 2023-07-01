package com.polzzak.domain.user.dto;

import com.polzzak.domain.user.entity.User;

public record UserDto(
	Long id,
	String userRole
) {
	public static UserDto from(final User user) {
		return new UserDto(user.getId(), user.getUserRole().toString());
	}
}
