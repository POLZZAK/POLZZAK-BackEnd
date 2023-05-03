package com.polzzak.user.model;

public record UserAccountResponse(
    String nickname,
    SocialType socialType
) {
}
