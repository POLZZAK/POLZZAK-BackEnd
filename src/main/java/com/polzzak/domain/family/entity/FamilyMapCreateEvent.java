package com.polzzak.domain.family.entity;

import java.util.List;

import com.polzzak.domain.user.entity.Member;

public record FamilyMapCreateEvent(
	List<Member> members
) {
}
