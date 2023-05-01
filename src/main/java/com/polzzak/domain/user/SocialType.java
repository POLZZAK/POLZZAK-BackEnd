package com.polzzak.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialType {
    KAKAO("카카오"), GOOGLE("구글"), APPLE("애플");

    private final String description;
}
