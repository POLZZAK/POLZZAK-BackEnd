package com.polzzak.domain.notification.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.polzzak.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	Slice<Notification> findNotificationsByReceiverIdAndIdLessThan(final long memberId, final long startId,
		final Pageable pageable);

	@Modifying
	@Query("UPDATE Notification n SET n.status = :status WHERE n.id IN :ids")
	void updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") Notification.Status status);

	@Query(nativeQuery = true, value = """
		SELECT n.id
		FROM notification n
		WHERE n.sender_id = :senderId AND n.receiver_id = :receiverId AND `type` = 'FAMILY_REQUEST'
		ORDER BY n.id DESC
		LIMIT 1""")
	Long selectIdBySenderIdAndReceiverIdAndStatus(@Param("senderId") Long senderId,
		@Param("receiverId") Long receiverId);

	List<Notification> findByIdIn(List<Long> ids);

	void deleteByIdIn(List<Long> ids);
}
