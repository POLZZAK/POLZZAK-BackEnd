package com.polzzak.domain.memberpoint.dto;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.polzzak.domain.memberpoint.entity.MemberPointHistory;

public record MemberPointHistorySliceResponse(
	Long startId,
	List<MemberPointHistoryResponse> content
) {
	public static MemberPointHistorySliceResponse from(final Slice<MemberPointHistory> slice, final Pageable pageable) {
		if (slice.hasNext()) {
			return new MemberPointHistorySliceResponse(
				slice.getContent().get(pageable.getPageSize() - 1).getId(),
				slice.map(MemberPointHistoryResponse::from).getContent()
			);
		}

		return new MemberPointHistorySliceResponse(
			null,
			slice.map(MemberPointHistoryResponse::from).getContent()
		);
	}
}
