package com.polzzak.domain.mission.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.mission.entity.Mission;

public interface MissionRepository extends JpaRepository<Mission, Long> {

	List<Mission> findByIdIn(List<Long> ids);
}
