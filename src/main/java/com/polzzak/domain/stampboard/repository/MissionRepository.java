package com.polzzak.domain.stampboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.stampboard.entity.Mission;

public interface MissionRepository extends JpaRepository<Mission, Long> {

	List<Mission> findByIdIn(List<Long> ids);

	void deleteByStampBoardId(long stampBoardId);
}
