package com.polzzak.domain.notification.entity;

import com.polzzak.domain.model.BaseEntity;
import com.polzzak.domain.user.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "notification")
public class Notification extends BaseEntity {

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private NotificationType type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id")
	private Member sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id")
	private Member receiver;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Status status;

	@Column(nullable = false)
	private String data;

	@Builder(builderMethodName = "createNotification")
	public Notification(final NotificationType type, final Member sender, final Member receiver, final String data) {
		this.type = type;
		this.sender = sender;
		this.receiver = receiver;
		this.data = data;
		this.status = Status.UNREAD;
	}

	public Long getRequestFamilyId() {
		if (type == NotificationType.FAMILY_REQUEST || type == NotificationType.FAMILY_REQUEST_COMPLETE) {
			return sender.getId();
		}
		return null;
	}

	@RequiredArgsConstructor
	public enum Status {
		READ("읽음"), UNREAD("안 읽음"), REQUEST_FAMILY("연동 요청"), REQUEST_FAMILY_ACCEPT("연동 수락"), REQUEST_FAMILY_REJECT(
			"연동 거절");

		private final String description;
	}
}
