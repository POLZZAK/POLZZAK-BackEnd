package com.polzzak.domain.family.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FamilyStatus {
	NONE("NONE"), RECEIVED("나에게 요청 보낸 사람"), SENT("내가 요청 보낸 사람"), APPROVE("승인");

	private final String description;
}
