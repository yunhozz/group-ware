package com.notificationservice.persistence.repository;

import com.notificationservice.dto.response.NotificationSimpleResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NotificationCustomRepository {

    Slice<NotificationSimpleResponseDto> findSliceByUserIdAndCheckStatus(String userId, Boolean isChecked, Long cursorId, Pageable pageable);
}