package com.polzzak.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	@Query("select u from User u join fetch Member m where u.username = :username")
	Optional<User> findByUsername(@Param("username") String username);

	@Query("select m.id from Member m where m.nickname = :nickname")
	Optional<Long> existsByNickname(@Param("nickname") String nickname);

	void deleteByMember(Member member);
}
