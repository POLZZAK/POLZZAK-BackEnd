package com.polzzak.global.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {
	PROFILE_IMAGE("프로필");

	private final String description;
}
