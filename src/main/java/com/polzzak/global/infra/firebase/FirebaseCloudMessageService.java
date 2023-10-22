package com.polzzak.global.infra.firebase;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(readOnly = true)
public class FirebaseCloudMessageService {

	private final PushTokenService pushTokenService;

	public void sendPushNotification(Member member, String title, String body, String link) {
		try {
			// FileInputStream serviceAccount = new FileInputStream(
			// 	"src/main/resources/firebase/firebase_service_key.json");
			log.info("[push]send push start.");
			String serviceKey = "{\n" + "  \"type\": \"service_account\",\n" + "  \"project_id\": \"polzzak-57648\",\n"
				+ "  \"private_key_id\": \"ead4311d449ac5facc6c507ec152a6d9f54a279d\",\n"
				+ "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAo"
				+ "IBAQCuffOTpdr5Z23/\\nn6c37jA+TkjDj8MZxBr67ovNVgKSUchidJe3A8VYS+Ck6tMaMr7/YPUu6OfkkHOS\\n0VlW3UQwYRq"
				+ "j5Rk5kH6ByBxVH9ZI7Sh0FO9pH71dsHZXwVLam6UOKcyrrbJhwcfX\\nXrnksdCi4LxhvIxTsn0z11Q3CXtf5diKXgIt+Ewgqk/"
				+ "sKWFmCLSjIATD6mMn5VTl\\nD8+QyOCV/U6ec3CmXHj2RAJMm1FiIE0vHDaGwNtc6hha/igibK6OoQ5+lyrnvuMr\\ngGS3o2zH"
				+ "5TOBfMs0FI9KrHaUgxfa0p9jG4Oze1ibyZhK+6hl1RSBSWuxy5iY77vp\\n4fdrzzexAgMBAAECggEACaYdWgTJ3xDBHGmPraAW"
				+ "OtvJWkcQ2tPlSgr24BvpeH3d\\nPtSDrzMeLovDmFsD4Wb8+NI7vKRUbmcufOfmsM77flFgT7/TbUN4O2T9bBeemdnD\\naufddU"
				+ "q0BgJECQY/tqb0sZvOHZA1VQKKMnaigOr0Ro123VC30ckE82Ds3z4+/EZ5\\n4+KKxVxXMiV3seQOHIdiMHI/ClrUTrr7S7h67U"
				+ "NJ6ZFzuP/JD1j7XENO9lM2haCL\\ny3uxa7KEHDJWs1DU6O8FsXaxdb/XCMAYYTnH33SBtnUxgHNmdbv6IyZfTi/Wm+rY\\nC8V"
				+ "rua52T+8VRav1btVCVNQE6RpNWgWFGGq8pAy1oQKBgQDyDUNyfnfK/xZJAZ9N\\nKMe7uBhrS95PAF33fHF6Kc70WPO9JLxWnXF"
				+ "NJPiT9bCRer7Vc9fkB388N43BnGX9\\nYDR68EySd2GA8C2BloM6/pBULg7u4CxOrbcaWgqkY8oEktM7cgn5I2EVOGVwTfwP\\n"
				+ "420ApXUS5zdUIGR/XsUROBf+hwKBgQC4jAyHNzGhczEALhbLTB+4gZqRO3J1z2uH\\n4msAyyD5dU/Vrpa+i+HwG/wWELAzIRbF"
				+ "bwL8pEa4qtbD+ofDN3ytpn86KQZrwxK2\\n0m89ajadwppeOh2z051TGCeU1cyECkv3iUPfZP+z9fERnNdAt+cly462zgaH+uJ"
				+ "T\\n2+/luP0uBwKBgQCJBhEkg4t1EyqecZioqWlIT1MjinNy7ZZEP+JNcdWCZci1TlKA\\nBejZ7w/5UqB9+qqFU2rn34abpCd"
				+ "PbyYdZZTP87ClSYec4logfgAUKX+y58/0UltC\\nvvxkooxbu1HlfOivQkN7EhgnVyG1jbAfnnNaZk/8P4AG07+QiymsMcEDiQ"
				+ "KBgAyx\\ntXrnlQZiAhDdGrxJNDVg1N0AldL8vYzPSkT3tAD0zNUJ+VyKCrSVeDWcWEJsGEDk\\nbfQq6KJzPeqlJQmMm4rmVQ"
				+ "IPKF3pQTRKLVSwJamcZTnuDXT9LWk11CMswbCjdK5G\\nRuDq9ZvPYxGvFC9jdwbmhZ6VdWWNIFxcWJgYrXGpAoGBAOwyu9yccz"
				+ "HwhaiITVnH\\nDmG7S2LCj8Jq3u3351c6dv/Lt8wuZ6u2FmTuMh8id9hD7u2TO9QzIF8+S10Rc/J8\\nzb9GI3KLg67bCCrReZ"
				+ "Q0B1L2cjKUObpx5om2icxYXTV1k3GyVg7RJwQ5NA8ncQSP\\nvXysN6nV8rcLOSuK8iGsS9Je\\n-----END PRIVATE KEY--"
				+ "---\\n\",\n"
				+ "  \"client_email\": \"firebase-adminsdk-mzmu4@polzzak-57648.iam.gserviceaccount.com\",\n"
				+ "  \"client_id\": \"101961288027838881996\",\n"
				+ "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n"
				+ "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n"
				+ "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n"
				+ "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-mzmu4%40polzzak-57648.iam.gserviceaccount.com\",\n"
				+ "  \"universe_domain\": \"googleapis.com\"\n" + "}\n";

			ByteArrayInputStream inputStream = new ByteArrayInputStream(serviceKey.getBytes(StandardCharsets.UTF_8));

			FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(inputStream))
				.build();
			log.info("[push] ready key.");
			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
			}
			log.info("[push]firebase initialized.");
			List<String> registrationTokens = pushTokenService.getPushTokens(member).stream()
				.map(PushToken::getToken)
				.toList();
			log.info("[push]push. token: {}, title: {}, body: {}, link: {}", registrationTokens, title, body, link);

			MulticastMessage message = MulticastMessage.builder()
				.setNotification(Notification.builder()
					.setTitle(title)
					.setBody(body)
					.build())
				.addAllTokens(registrationTokens)
				.putData("link", link)
				.build();
			BatchResponse response = null;
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
