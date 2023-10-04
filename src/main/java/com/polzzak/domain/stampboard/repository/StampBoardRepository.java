package com.polzzak.domain.stampboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.polzzak.domain.stampboard.entity.StampBoard;
import com.polzzak.domain.user.entity.Member;

public interface StampBoardRepository extends JpaRepository<StampBoard, Long> {

	List<StampBoard> findByGuardianIdAndKidIdAndIsDeleted(Long guardianId, Long kidId, boolean isDeleted);

	StampBoard findByIdAndIsDeleted(Long id, boolean isDeleted);

	@Modifying
	@Query("UPDATE StampBoard s SET s.isDeleted = true WHERE s.kidId = :memberId OR s.guardianId = :memberId")
	void deleteByKidIdOrGuardianId(@Param("memberId") long memberId);
}
