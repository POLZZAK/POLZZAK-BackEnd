package com.polzzak.global.infra.firebase.dto;

import java.util.Map;

import lombok.Getter;

@Getter
public class Message {

	private Notification notification;
	private String to;
	private Map<String, String> data;

	public Message(Notification notification, String to, Map<String, String> data) {
		this.notification = notification;
		this.to = to;
		this.data = data;
	}
}
