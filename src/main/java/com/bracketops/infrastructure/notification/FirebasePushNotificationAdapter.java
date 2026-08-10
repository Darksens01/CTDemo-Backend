package com.bracketops.infrastructure.notification;

import com.bracketops.domain.port.outbound.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Primary
public class FirebasePushNotificationAdapter implements NotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(FirebasePushNotificationAdapter.class);

    private final RestTemplate restTemplate;
    private final String fcmServerKey;

    public FirebasePushNotificationAdapter(@Value("${app.firebase.server-key:}") String fcmServerKey) {
        this.restTemplate = new RestTemplate();
        this.fcmServerKey = fcmServerKey;
    }

    @Override
    public void sendMatchUpdateNotification(String tournamentName, String roundName, String winnerTeamName, String loserTeamName, int winnerScore, int loserScore) {
        String title = "🏆 " + tournamentName + " - " + roundName;
        String body = winnerTeamName + " defeated " + loserTeamName + " (" + winnerScore + " - " + loserScore + ")!";

        logger.info("[FIREBASE PUSH NOTIFICATION] Title: '{}' | Body: '{}'", title, body);

        if (fcmServerKey != null && !fcmServerKey.trim().isEmpty()) {
            try {
                sendFcmPushPayload(title, body, "/topics/esports-matches");
            } catch (Exception e) {
                logger.warn("Failed to dispatch Firebase FCM push notification: {}", e.getMessage());
            }
        }
    }

    @Override
    public void sendChampionCrownedNotification(String tournamentName, String championTeamName) {
        String title = "👑 CHAMPION CROWNED!";
        String body = "Team " + championTeamName + " won the " + tournamentName + " Tournament!";

        logger.info("[FIREBASE PUSH NOTIFICATION] Title: '{}' | Body: '{}'", title, body);

        if (fcmServerKey != null && !fcmServerKey.trim().isEmpty()) {
            try {
                sendFcmPushPayload(title, body, "/topics/esports-champions");
            } catch (Exception e) {
                logger.warn("Failed to dispatch Firebase FCM push notification: {}", e.getMessage());
            }
        }
    }

    @Override
    public void sendTournamentCancelledNotification(String tournamentName, String reason) {
        String title = "⚠️ TORNEO CANCELADO!";
        String body = "El torneo " + tournamentName + " fue cancelado. Motivo: " + reason;

        logger.info("[FIREBASE PUSH NOTIFICATION] Title: '{}' | Body: '{}'", title, body);

        if (fcmServerKey != null && !fcmServerKey.trim().isEmpty()) {
            try {
                sendFcmPushPayload(title, body, "/topics/esports-announcements");
            } catch (Exception e) {
                logger.warn("Failed to dispatch Firebase FCM push notification: {}", e.getMessage());
            }
        }
    }

    private void sendFcmPushPayload(String title, String body, String topic) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "key=" + fcmServerKey);

        Map<String, Object> notificationMap = new HashMap<>();
        notificationMap.put("title", title);
        notificationMap.put("body", body);
        notificationMap.put("icon", "https://bracketops.gg/icon.png");

        Map<String, Object> payload = new HashMap<>();
        payload.put("to", topic);
        payload.put("notification", notificationMap);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        restTemplate.postForEntity("https://fcm.googleapis.com/fcm/send", entity, String.class);
    }
}
