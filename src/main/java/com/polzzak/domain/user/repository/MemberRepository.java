package com.polzzak.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.polzzak.domain.user.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	@Query("select m from Member m where m.nickname = :nickname")
	Optional<Member> searchByNickname(@Param("nickname") final String nickname);
}
