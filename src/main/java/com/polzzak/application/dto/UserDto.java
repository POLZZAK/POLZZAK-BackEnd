package com.polzzak.application.dto;

import com.polzzak.domain.user.MemberType;
import com.polzzak.domain.user.SocialType;
import com.polzzak.domain.user.User;

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
