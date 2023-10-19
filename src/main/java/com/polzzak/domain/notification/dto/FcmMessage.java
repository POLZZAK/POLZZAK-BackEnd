package com.polzzak.domain.notification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FcmMessage {

	private boolean validateOnly;
	private Message message;
	private String to;

	public FcmMessage(boolean validateOnly, Message message, String to) {
		this.validateOnly = validateOnly;
		this.message = message;
		this.to = to;
	}

	@NoArgsConstructor
	@Getter
	public static class Message {
		private Notification notification;
		private String token;

		public Message(Notification notification, String token) {
			this.notification = notification;
			this.token = token;
		}
	}

	@NoArgsConstructor
	@Getter
	public static class Notification {
		private String title;
		private String body;
		private String image;

		public Notification(String title, String body, String image) {
			this.title = title;
			this.body = body;
			this.image = image;
		}
	}
}
