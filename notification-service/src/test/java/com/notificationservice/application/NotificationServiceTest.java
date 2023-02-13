package com.notificationservice.application;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.notificationservice.application.exception.NotificationNotFoundException;
import com.notificationservice.dto.request.NotificationRequestDto;
import com.notificationservice.dto.response.NotificationResponseDto;
import com.notificationservice.persistence.Notification;
import com.notificationservice.persistence.repository.EmitterRepository;
import com.notificationservice.persistence.repository.NotificationRepository;
import com.notificationservice.util.LoggerTestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@Slf4j
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    NotificationService notificationService;

    @Mock
    NotificationRepository notificationRepository;

    @Mock
    EmitterRepository emitterRepository;

    @Test
    @DisplayName("sse emitter 연결")
    void connect() throws Exception {
        // given
        String userId = "userId";
        ListAppender<ILoggingEvent> listAppender = LoggerTestUtil.getListAppenderForClass(NotificationService.class);

        // when
        SseEmitter result = notificationService.connect(userId, null);
        List<ILoggingEvent> logList = listAppender.list;
        log.info("전체 log : {}",
                new ObjectMapper().registerModule(new JavaTimeModule())
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(logList));

        // then
        assertDoesNotThrow(() -> result);
        assertThat(result.getTimeout()).isEqualTo(60 * 60 * 1000L);
        assertThat(logList.get(0).getMessage()).contains("Data send to " + userId);
    }

    @Test
    @DisplayName("알림 보내기")
    void sendMessage() throws Exception {
        // given
        String senderId = "senderId";
        String receiverId = "receiverId";
        NotificationRequestDto notificationRequestDto = new NotificationRequestDto(receiverId, "This is test");
        Notification notification = new Notification(senderId, notificationRequestDto.getReceiverId(), notificationRequestDto.getMessage());

        Map<String, SseEmitter> emitters = new ConcurrentHashMap<>() {{
            put(receiverId, new SseEmitter(1000L));
        }};
        ListAppender<ILoggingEvent> listAppender = LoggerTestUtil.getListAppenderForClass(NotificationService.class);

        given(emitterRepository.findEmittersByUserId(anyString())).willReturn(emitters);
        given(notificationRepository.save(any(Notification.class))).willReturn(notification);

        // when
        Long result = notificationService.sendMessage(senderId, notificationRequestDto);
        List<ILoggingEvent> logList = listAppender.list;
        log.info("전체 log : {}",
                new ObjectMapper().registerModule(new JavaTimeModule())
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(logList));

        // then
        assertDoesNotThrow(() -> result);
        assertThat(result).isEqualTo(notification.getId());
        assertThat(logList.get(0).getMessage()).contains("Data send to " + receiverId);
    }

    @Test
    @DisplayName("특정 알림 조회")
    void getNotificationDto() throws Exception {
        // given
        String senderId = "senderId";
        String receiverId = "receiverId";
        Long notificationId = 100L;
        Notification notification = new Notification(senderId, receiverId, "This is test");

        given(notificationRepository.findById(anyLong())).willReturn(Optional.of(notification));

        // when
        NotificationResponseDto result = notificationService.getNotificationDto(notificationId);

        // then
        assertDoesNotThrow(() -> result);
        assertThat(result.getId()).isEqualTo(notification.getId());
        assertThat(result.getSenderId()).isEqualTo(senderId);
        assertThat(result.getReceiverId()).isEqualTo(receiverId);
        assertThat(result.getIsChecked()).isEqualTo(true);
    }

    @Test
    @DisplayName("특정 알림 조회 실패")
    void getNotificationThrowNotificationNotFoundException() throws Exception {
        // given
        Long notificationId = 100L;
        given(notificationRepository.findById(anyLong())).willReturn(Optional.empty());

        // then
        assertThrows(NotificationNotFoundException.class, () -> notificationService.getNotificationDto(notificationId));
    }

    @Test
    @DisplayName("특정 알림 삭제")
    void deleteNotification() throws Exception {
        // given
        String senderId = "senderId";
        String receiverId = "receiverId";
        Notification notification = new Notification(senderId, receiverId, "This is test");

        willDoNothing().given(notificationRepository).deleteById(notification.getId());

        // then
        assertDoesNotThrow(() -> notificationService.deleteNotification(notification.getId()));
    }

    @Test
    @DisplayName("이미 읽은 알림 삭제")
    void deleteNotificationsAlreadyChecked() throws Exception {
        // given
        String receiverId = "receiverId";
        Long notificationId1 = 100L;
        Long notificationId2 = 200L;

        given(notificationRepository.findIdsByUserIdAndAlreadyChecked(anyString())).willReturn(List.of(notificationId1, notificationId2));
        willDoNothing().given(notificationRepository).deleteInIds(List.of(notificationId1, notificationId2));

        // then
        assertDoesNotThrow(() -> notificationService.deleteNotificationsAlreadyChecked(receiverId));
    }
}