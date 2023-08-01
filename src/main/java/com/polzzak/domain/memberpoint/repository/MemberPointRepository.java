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

import jakarta.persistence.LockModeType;

public interface MemberPointRepository extends JpaRepository<MemberPoint, Long> {
	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	Optional<MemberPoint> findWithWriteLockByMemberId(final Long memberId);

	@Query("select mp from MemberPoint mp where mp.member.memberType.type = :memberType order by mp.point desc")
	List<MemberPoint> findGuardianPointRankingTop30(final @Param("memberType") MemberType memberType,
		Pageable pageable);
}
