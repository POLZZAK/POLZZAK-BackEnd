package com.polzzak.file.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FileType {
    PROFILE_IMAGE("프로필");

    private final String description;
}
