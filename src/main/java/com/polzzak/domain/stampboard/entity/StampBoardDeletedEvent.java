package com.polzzak.domain.stampboard.entity;

import com.polzzak.domain.user.entity.Member;

public record StampBoardDeletedEvent(
	Member guardian
) {
}
