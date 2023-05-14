package com.polzzak.domain.family.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.polzzak.domain.family.entity.FamilyTempMap;

public interface FamilyTempMapRepository extends JpaRepository<FamilyTempMap, Long> {
	void deleteBySenderIdAndReceiverId(final Long senderId, final Long receiverId);

	List<FamilyTempMap> findAllBySenderId(final Long senderId);

	List<FamilyTempMap> findAllByReceiverId(final Long receiverId);

	@Query("select ftm.id from FamilyTempMap ftm where ftm.senderId = :senderId and ftm.receiverId = :receiverId")
	Optional<Long> existsBySenderAndReceiverId(@Param("senderId") final Long senderId,
		@Param("receiverId") final Long receiverId);
}
