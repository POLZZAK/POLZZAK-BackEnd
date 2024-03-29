package com.polzzak.domain.family.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.polzzak.domain.family.entity.FamilyMap;
import com.polzzak.domain.user.entity.Member;

public interface FamilyMapRepository extends JpaRepository<FamilyMap, Long> {
	@Query("select fm from FamilyMap fm where fm.kid.id = :kidId")
	List<FamilyMap> getFamilyMapsByKidId(@Param("kidId") final Long kidId);

	@Query("select fm from FamilyMap fm where fm.guardian.id = :guardianId")
	List<FamilyMap> getFamilyMapsByGuardianId(@Param("guardianId") final Long guardianId);

	boolean existsByGuardianIdAndKidId(Long guardianId, Long kidId);

	void deleteByGuardianIdAndKidId(Long guardianId, Long kidId);

	int countByKidId(Long kidId);

	int countByGuardianId(Long guardianId);

	void deleteByGuardian(final Member guardian);

	void deleteByKid(final Member kid);
}
