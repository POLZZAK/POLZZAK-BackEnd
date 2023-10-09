package com.polzzak.domain.pushtoken.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.pushtoken.model.PushToken;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {

}
