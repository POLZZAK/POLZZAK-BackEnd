package com.polzzak.domain.memberpoint.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.polzzak.domain.memberpoint.entity.MemberPoint;

import jakarta.persistence.LockModeType;

public interface MemberPointRepository extends JpaRepository<MemberPoint, Long> {
	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	Optional<MemberPoint> findWithWriteLockByMemberId(final Long memberId);
}
