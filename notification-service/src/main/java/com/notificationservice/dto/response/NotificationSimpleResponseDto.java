package com.notificationservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NotificationSimpleResponseDto {

    private Long id;
    @JsonIgnore
    private String senderId;
    private String message;
    private LocalDateTime createdAt;
    private UserSimpleResponseDto userInfo;

    @QueryProjection
    public NotificationSimpleResponseDto(Long id, String senderId, String message, LocalDateTime createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public void setUserInfo(UserSimpleResponseDto userInfo) {
        this.userInfo = userInfo;
    }
}