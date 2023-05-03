package com.polzzak.user.model;

import com.polzzak.member.model.MemberType;

public record UserDto(
    String username,
    String nickname,
    MemberType memberType,
    SocialType socialType,
    String profileUrl
) {
    public static UserDto from(final User user, final String profileUrl) {
        return new UserDto(user.getUsername(), user.getMember().getNickname(), user.getMember().getMemberType(), user.getSocialType(), profileUrl);
    }
}
