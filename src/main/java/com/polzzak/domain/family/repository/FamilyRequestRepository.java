package com.polzzak.domain.family.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.polzzak.domain.family.entity.FamilyRequest;
import com.polzzak.domain.user.entity.Member;

public interface FamilyRequestRepository extends JpaRepository<FamilyRequest, Long> {
	void deleteBySenderIdAndReceiverId(final Long senderId, final Long receiverId);

	List<FamilyRequest> findAllBySenderId(final Long senderId);

	List<FamilyRequest> findAllByReceiverId(final Long receiverId);

	boolean existsBySenderIdAndReceiverId(@Param("senderId") final Long senderId,
		@Param("receiverId") final Long receiverId);

	boolean existsByReceiver(final Member receiver);

	boolean existsBySender(final Member sender);
}
