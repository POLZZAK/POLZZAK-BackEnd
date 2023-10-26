package com.polzzak.global.infra.firebase.service;

import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polzzak.domain.pushtoken.model.PushToken;
import com.polzzak.domain.pushtoken.service.PushTokenService;
import com.polzzak.domain.user.entity.Member;
import com.polzzak.global.infra.firebase.dto.Message;
import com.polzzak.global.infra.firebase.dto.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FirebaseCloudMessageService {

	private final PushTokenService pushTokenService;
	private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
	private static final String SERVER_KEY = "AAAAXWWv8O0:APA91bEPp9GZzMUjTmhmk9n0J5PuX1LvAf-Kaa-vCffntaV85klO-"
		+ "gLb4QLf4f9ohrjdIu6L7MOZbaKUReN8CgljU4t1vWWS6BFESQhpZVPQzLhtswju1naFbmHyVNzrHvZWVpHvRNWo";

	public void sendPushNotification(Member member, String title, String body, String link) {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost httpPost = new HttpPost(FCM_URL);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("Authorization", "key=" + SERVER_KEY);

			List<String> registrationTokens = pushTokenService.getPushTokens(member).stream()
				.map(PushToken::getToken)
				.toList();

			for (String token : registrationTokens) {
				Notification notification = new Notification(title, body, "default");
				Map<String, String> data = Map.of("link", link == null ? "" : link, "title", title, "body", body);
				Message message = new Message(notification, token, data);
				ObjectMapper objectMapper = new ObjectMapper();

				String fcmMessage = objectMapper.writeValueAsString(message);
				httpPost.setEntity(new StringEntity(fcmMessage, "UTF-8"));
				httpClient.execute(httpPost);
			}
		} catch (Exception e) {
			// 예외 처리
			e.printStackTrace();
		}
	}
}
