package com.polzzak.domain.membertype.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.membertype.entity.MemberTypeDetail;

public interface MemberTypeDetailRepository extends JpaRepository<MemberTypeDetail, Long> {
	boolean existsByDetail(String detail);
}
