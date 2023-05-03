package com.polzzak.support;

import org.springframework.mock.web.MockMultipartFile;

public class UserFixtures {
    public static final String TEST_USERNAME = "username";
    public static final String TEST_NICKNAME = "nickname";
    public static final String TEST_PROFILE_URL = "profileUrl";
    public static final String TEST_DEFAULT_PROFILE_URL = "defaultProfileUrl";
    public static final MockMultipartFile TEST_PROFILE =
        new MockMultipartFile("profile", "originalFilename", "image/png", "content".getBytes());
}
