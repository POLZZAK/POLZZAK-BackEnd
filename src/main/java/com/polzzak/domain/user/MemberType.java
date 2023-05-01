package com.polzzak.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
    MOTHER("엄마"), FATHER("아빠"),
    SISTER("누나/언니"), BROTHER("형/오빠"),
    GRANDMOTHER("할머니"), GRANDFATHER("할아버지"),
    AUNT("이모/고모"), UNCLE("삼총"), ETC("보호자");

    private final String description;
}
