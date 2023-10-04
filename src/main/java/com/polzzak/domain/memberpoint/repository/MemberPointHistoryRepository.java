package com.polzzak.domain.memberpoint.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.polzzak.domain.memberpoint.entity.MemberPointHistory;

public interface MemberPointHistoryRepository extends JpaRepository<MemberPointHistory, Long> {
	Slice<MemberPointHistory> findSliceByMemberPointMemberId(final long memberId, final Pageable pageable);

	Slice<MemberPointHistory> findSliceByMemberPointMemberIdAndIdLessThan(final long memberId, final long startId,
		final Pageable pageable);

	@Modifying
	@Query("delete from MemberPointHistory mph where mph.memberPoint.memberId = :memberId")
	void deleteByMemberId(@Param("memberId") final Long memberId);
}
