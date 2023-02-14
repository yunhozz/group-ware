package com.notificationservice.persistence.repository;

import com.notificationservice.persistence.Notification;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Notification> notifications = new ConcurrentHashMap<>();

    public void saveEmitter(String emitterId, SseEmitter emitter) {
        emitters.put(emitterId, emitter);
    }

    public void saveNotification(String eventId, Notification notification) {
        notifications.put(eventId, notification);
    }

    public Map<String, SseEmitter> findEmittersByUserId(String userId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Notification> findNotificationsByUserId(String userId) {
        return notifications.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void deleteEmitterById(String emitterId) {
        emitters.keySet().forEach(key -> {
            if (key.equals(emitterId)) {
                emitters.remove(key);
            }
        });
    }

    public void deleteNotificationById(String eventId) {
        notifications.keySet().forEach(key -> {
            if (key.equals(eventId)) {
                notifications.remove(key);
            }
        });
    }
}