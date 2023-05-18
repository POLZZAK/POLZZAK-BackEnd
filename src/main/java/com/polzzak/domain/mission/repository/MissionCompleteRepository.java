package com.polzzak.domain.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.mission.entity.MissionComplete;

public interface MissionCompleteRepository extends JpaRepository<MissionComplete, Long> {
}
