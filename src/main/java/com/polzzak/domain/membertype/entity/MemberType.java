package com.polzzak.domain.membertype.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
	GUARDIAN("보호자"), KID("아이");
	private final String description;
}
