package com.polzzak.global.infra.firebase.dto;

import lombok.Getter;

@Getter
public class Notification {

	private String title;
	private String body;
	private String sound;

	public Notification(String title, String body, String sound) {
		this.title = title;
		this.body = body;
		this.sound = sound;
	}
}
