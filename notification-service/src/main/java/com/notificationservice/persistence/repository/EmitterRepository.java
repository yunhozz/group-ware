package com.notificationservice.persistence.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> events = new ConcurrentHashMap<>();

    public void saveEmitter(String emitterId, SseEmitter emitter) {
        emitters.put(emitterId, emitter);
    }

    public void saveEvent(String eventId, Object event) {
        events.put(eventId, event);
    }

    public Map<String, SseEmitter> findEmittersByUserId(String userId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Object> findEventsByUserId(String userId) {
        return events.entrySet().stream()
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

    public void deleteEventById(String eventId) {
        events.keySet().forEach(key -> {
            if (key.equals(eventId)) {
                emitters.remove(key);
            }
        });
    }
}