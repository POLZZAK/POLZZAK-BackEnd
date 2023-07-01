package com.polzzak.domain.memberpoint.entity;

import java.util.List;

import com.polzzak.domain.user.entity.Member;

public record MemberPointEvent(
	List<Member> members,
	MemberPointType memberPointType
) {
}
