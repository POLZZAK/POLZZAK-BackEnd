package com.polzzak.global.infra.firebase;

import java.io.FileInputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.polzzak.domain.pushtoken.model.PushToken;
import com.polzzak.domain.pushtoken.service.PushTokenService;
import com.polzzak.domain.user.entity.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

	private final PushTokenService pushTokenService;

	public void sendPushNotification(Member member, String title, String body, String link) {
		try {
			FileInputStream serviceAccount = new FileInputStream(
				"src/main/resources/firebase/firebase_service_key.json");

			FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();

			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
			}

			List<String> registrationTokens = pushTokenService.getPushTokens(member).stream()
				.map(PushToken::getToken)
				.toList();

			MulticastMessage message = MulticastMessage.builder()
				.setNotification(Notification.builder()
					.setTitle(title)
					.setBody(body)
					.build())
				.addAllTokens(registrationTokens)
				.putData("link", link)
				.build();
			BatchResponse response = null;
			log.info("push. token: {}, title: {}, body: {}, link: {}", registrationTokens, title, body, link);
			try {
				response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
			} catch (FirebaseMessagingException e) {
				log.warn("fail push.");
				e.printStackTrace();
			}
			// See the BatchResponse reference documentation
			// for the contents of response.
			log.info(response.getSuccessCount() + " messages were sent successfully");

		} catch (Exception e) {

		}
	}
}
