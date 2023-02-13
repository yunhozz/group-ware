package com.notificationservice.persistence.repository;

import com.notificationservice.dto.response.NotificationSimpleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationCustomRepository {

    Page<NotificationSimpleResponseDto> findListByUserIdAndCheckStatus(String userId, Boolean isChecked, Long lastNotificationId, Pageable pageable);
}