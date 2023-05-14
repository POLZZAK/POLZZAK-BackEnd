package com.polzzak.domain.family.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.polzzak.domain.family.entity.FamilyMap;

public interface FamilyMapRepository extends JpaRepository<FamilyMap, Long> {
	@Query("select fm from FamilyMap fm where fm.kidId = :kidId")
	List<FamilyMap> getFamilyMapsByKidId(@Param("kidId") final Long kidId);

	@Query("select fm from FamilyMap fm where fm.guardianId = :guardianId")
	List<FamilyMap> getFamilyMapsByGuardianId(@Param("guardianId") final Long guardianId);
}
