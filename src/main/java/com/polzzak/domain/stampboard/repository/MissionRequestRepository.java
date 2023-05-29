package com.polzzak.domain.stampboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.stampboard.entity.MissionRequest;

public interface MissionRequestRepository extends JpaRepository<MissionRequest, Long> {

	void deleteByStampBoardId(long stampBoardId);
}
