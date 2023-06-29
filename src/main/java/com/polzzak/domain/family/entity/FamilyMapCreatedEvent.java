package com.polzzak.domain.family.entity;

import java.util.List;

import com.polzzak.domain.user.entity.Member;

public record FamilyMapCreatedEvent(
	List<Member> members
) {
}
