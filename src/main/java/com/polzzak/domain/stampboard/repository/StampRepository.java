package com.polzzak.domain.stampboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.stampboard.entity.Stamp;

public interface StampRepository extends JpaRepository<Stamp, Long> {

	void deleteByStampBoardId(long stampBoardId);
}
