package com.polzzak.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
	MOTHER("엄마"), FATHER("아빠"), FEMALE_SISTER("언니"), MALE_SISTER("누나"),
	FEMALE_BROTHER("오빠"), MALE_BROTHER("형"), GRANDMOTHER("할머니"), GRANDFATHER("할아버지"),
	MATERNAL_AUNT("이모"), PATERNAL_AUNT("고모"), UNCLE("삼촌"), ETC("보호자"), KID("아이");

	private final String description;
}
