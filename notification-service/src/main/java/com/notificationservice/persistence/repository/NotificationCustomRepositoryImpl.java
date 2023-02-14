package com.notificationservice.persistence.repository;

import com.notificationservice.dto.response.NotificationSimpleResponseDto;
import com.notificationservice.dto.response.QNotificationSimpleResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.notificationservice.persistence.QNotification.notification;

@Repository
@RequiredArgsConstructor
public class NotificationCustomRepositoryImpl implements NotificationCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<NotificationSimpleResponseDto> findSliceByUserIdAndCheckStatus(String userId, Boolean isChecked, Long cursorId, Pageable pageable) {
        List<NotificationSimpleResponseDto> notifications = queryFactory
                .select(new QNotificationSimpleResponseDto(
                        notification.id,
                        notification.senderId,
                        notification.message,
                        notification.createdAt
                ))
                .from(notification)
                .where(
                        notification.receiverId.eq(userId),
                        checkStatusEq(isChecked),
                        notificationIdLt(cursorId)
                )
                .orderBy(notification.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (notifications.size() > pageable.getPageSize()) {
            notifications.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(notifications, pageable, hasNext);
    }

    private BooleanExpression checkStatusEq(Boolean isChecked) {
        return isChecked != null ? notification.isChecked.eq(isChecked) : null;
    }

    private BooleanExpression notificationIdLt(Long cursorId) {
        return cursorId != null ? notification.id.lt(cursorId) : null;
    }
}