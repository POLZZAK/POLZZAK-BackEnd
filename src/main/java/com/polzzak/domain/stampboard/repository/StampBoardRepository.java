package com.polzzak.domain.stampboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.stampboard.entity.StampBoard;

public interface StampBoardRepository extends JpaRepository<StampBoard, Long> {

	List<StampBoard> findByGuardianIdAndKidIdAndIsDeleted(Long guardianId, Long kidId, boolean isDeleted);

	StampBoard findByIdAndIsDeleted(Long id, boolean isDeleted);
}
