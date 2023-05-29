package com.polzzak.domain.stampboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.stampboard.entity.MissionComplete;

public interface MissionCompleteRepository extends JpaRepository<MissionComplete, Long> {

	void deleteByIdIn(List<Long> ids);
}
