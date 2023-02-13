package com.notificationservice.dto.response;

import com.notificationservice.persistence.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {

    private Long id;
    private String senderId;
    private String receiverId;
    private String message;
    private Boolean isChecked;
    private LocalDateTime createdAt;

    public NotificationResponseDto(Notification notification) {
        id = notification.getId();
        senderId = notification.getSenderId();
        receiverId = notification.getReceiverId();
        message = notification.getMessage();
        isChecked = notification.isChecked();
        createdAt = notification.getCreatedAt();
    }
}