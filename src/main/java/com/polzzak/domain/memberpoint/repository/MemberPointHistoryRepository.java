package com.polzzak.domain.memberpoint.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.memberpoint.entity.MemberPointHistory;

public interface MemberPointHistoryRepository extends JpaRepository<MemberPointHistory, Long> {
	Slice<MemberPointHistory> findSliceByMemberPointMemberId(final long memberId, final Pageable pageable);

	Slice<MemberPointHistory> findSliceByMemberPointMemberIdAndIdLessThan(final long memberId, final long startId,
		final Pageable pageable);
}
