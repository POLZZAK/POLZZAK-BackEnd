package com.polzzak.domain.memberpoint.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.polzzak.domain.memberpoint.entity.MemberPoint;
import com.polzzak.domain.membertype.entity.MemberType;
import com.polzzak.domain.user.entity.Member;

import jakarta.persistence.LockModeType;

public interface MemberPointRepository extends JpaRepository<MemberPoint, Long> {
	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	Optional<MemberPoint> findWithWriteLockByMemberId(final Long memberId);

	@Query("select mp from MemberPoint mp where mp.member.memberType.type = :memberType order by mp.point desc")
	List<MemberPoint> findMemberPointRankings(final @Param("memberType") MemberType memberType,
		Pageable pageable);

	@Query(value = "SELECT ranking "
		+ "FROM (SELECT member_id, RANK() OVER (ORDER BY point DESC) AS ranking FROM member_point) AS rankings "
		+ "WHERE member_id = :memberId", nativeQuery = true)
	int getPointRankingByMemberId(Long memberId);

	void deleteByMember(final Member member);
}
