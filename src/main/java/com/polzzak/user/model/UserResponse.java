package com.polzzak.user.model;

import com.polzzak.member.model.MemberType;

public record UserResponse(
    String nickname,
    MemberType memberType,
    String profileUrl
) {
    public static UserResponse from(final UserDto userDto) {
        return new UserResponse(userDto.nickname(), userDto.memberType(), userDto.profileUrl());
    }
}
