package com.polzzak.domain.pushtoken.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.pushtoken.model.PushToken;
import com.polzzak.domain.user.entity.Member;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {

	List<PushToken> getPushTokensByMember(Member member);
}
