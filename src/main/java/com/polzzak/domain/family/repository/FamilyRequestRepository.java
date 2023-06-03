package com.polzzak.domain.family.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.polzzak.domain.family.entity.FamilyRequest;

public interface FamilyRequestRepository extends JpaRepository<FamilyRequest, Long> {
	void deleteBySenderIdAndReceiverId(final Long senderId, final Long receiverId);

	List<FamilyRequest> findAllBySenderId(final Long senderId);

	List<FamilyRequest> findAllByReceiverId(final Long receiverId);

	@Query("select fr.id from FamilyRequest fr where fr.sender.id = :senderId and fr.receiver.id = :receiverId")
	Optional<Long> existsBySenderAndReceiverId(@Param("senderId") final Long senderId,
		@Param("receiverId") final Long receiverId);
}
