package com.polzzak.domain.stampboard.entity;

import java.util.List;

import com.polzzak.domain.user.entity.Member;

public record StampCreatedEvent(
	List<Member> members
) {
}
