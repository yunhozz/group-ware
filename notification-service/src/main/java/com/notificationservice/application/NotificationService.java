package com.notificationservice.application;

import com.notificationservice.application.exception.DataSendFailException;
import com.notificationservice.application.exception.NotificationNotFoundException;
import com.notificationservice.dto.request.NotificationRequestDto;
import com.notificationservice.dto.response.NotificationResponseDto;
import com.notificationservice.persistence.Notification;
import com.notificationservice.persistence.repository.EmitterRepository;
import com.notificationservice.persistence.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;

    private final static Long EMITTER_TIMEOUT = 60 * 60 * 1000L; // 1 hour

    @Transactional(readOnly = true)
    public SseEmitter connect(String userId, String lastEventId) {
        String emitterId = userId + "_" + System.currentTimeMillis();
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT);
        emitterRepository.saveEmitter(emitterId, emitter);

        emitter.onCompletion(() -> complete(emitterId));
        emitter.onTimeout(() -> complete(emitterId));
        sendToClient(emitter, emitterId, "Event Stream Created. [user id = " + userId + "]");

        if (lastEventId != null) {
            emitterRepository.findNotificationsByUserId(userId).entrySet().stream()
                    .filter(entry -> entry.getKey().compareTo(lastEventId) > 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    @Transactional
    public Long sendMessage(String senderId, NotificationRequestDto notificationRequestDto) {
        Notification notification = new Notification(senderId, notificationRequestDto.getReceiverId(), notificationRequestDto.getMessage());
        NotificationResponseDto notificationResponseDto = new NotificationResponseDto(notification);

        emitterRepository.findEmittersByUserId(notificationResponseDto.getReceiverId()).forEach((emitterId, emitter) -> {
            emitterRepository.saveNotification(emitterId, notification);
            sendToClient(emitter, notificationResponseDto.getReceiverId(), notificationResponseDto);
        });

        return notificationRepository.save(notification).getId();
    }

    @Transactional(readOnly = true)
    public NotificationResponseDto getNotificationDto(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(NotificationNotFoundException::new);
        notification.check();
        return new NotificationResponseDto(notification);
    }

    @Transactional
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    @Transactional
    public void deleteNotificationsAlreadyChecked(String userId) {
        List<Long> ids = notificationRepository.findIdsByUserIdAndAlreadyChecked(userId);
        notificationRepository.deleteInIds(ids);
    }

    private void complete(String emitterId) {
        log.info("Emitter Completed");
        emitterRepository.deleteEmitterById(emitterId);
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(
                    SseEmitter.event()
                            .id(emitterId)
                            .name("sse")
                            .data(data)
                            .build());

            log.info("Data send to " + emitterId);

        } catch (IOException e) {
            emitterRepository.deleteEmitterById(emitterId);
            throw new DataSendFailException();
        }
    }
}