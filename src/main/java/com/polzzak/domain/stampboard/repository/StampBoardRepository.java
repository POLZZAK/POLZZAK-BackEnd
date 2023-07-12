package com.polzzak.domain.stampboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.polzzak.domain.stampboard.entity.StampBoard;

public interface StampBoardRepository extends JpaRepository<StampBoard, Long> {

	List<StampBoard> findByGuardianIdAndKidId(Long guardianId, Long kidId);

	@Modifying
	@Query("UPDATE StampBoard sb SET sb.isDeleted = :isDeleted WHERE sb.id = :id")
	void updateIsDeletedById(Long id, boolean isDeleted);

	StampBoard findByIdAndIsDeleted(Long id, boolean isDeleted);
}
