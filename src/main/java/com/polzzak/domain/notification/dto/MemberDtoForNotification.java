package com.polzzak.domain.notification.dto;

import com.polzzak.domain.user.entity.Member;

public record MemberDtoForNotification(long id, String nickname, String profileUrl) {

	public static MemberDtoForNotification from(final Member member, final String profileUrl) {
		return new MemberDtoForNotification(member.getId(), member.getNickname(), profileUrl);
	}
}
