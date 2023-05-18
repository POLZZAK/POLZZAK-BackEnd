package com.polzzak.domain.stamp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.polzzak.domain.stamp.entity.StampBoard;

public interface StampBoardRepository extends JpaRepository<StampBoard, Long> {

	List<StampBoard> findByGuardianIdAndKidId(Long guardianId, Long kidId);
}
