package com.polzzak.domain.stampboard.entity;

import com.polzzak.domain.user.entity.Member;

public record StampBoardDeleteEvent(
	Member guardian
) {
}
