package com.notificationservice.persistence.repository;

import com.notificationservice.dto.response.NotificationSimpleResponseDto;
import com.notificationservice.dto.response.QNotificationSimpleResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.notificationservice.persistence.QNotification.notification;

@Repository
@RequiredArgsConstructor
public class NotificationCustomRepositoryImpl implements NotificationCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<NotificationSimpleResponseDto> findListByUserIdAndCheckStatus(String userId, Boolean isChecked, Long lastNotificationId, Pageable pageable) {
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
                        byCheckStatus(isChecked)
                )
                .where(notificationIdLoe(lastNotificationId))
                .orderBy(notification.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(notifications, pageable, getTotalCount());
    }

    private BooleanExpression byCheckStatus(Boolean isChecked) {
        return isChecked != null ? notification.isChecked.eq(isChecked) : null;
    }

    private BooleanExpression notificationIdLoe(Long lastId) {
        return lastId != null ? notification.id.loe(lastId) : null;
    }

    private Long getTotalCount() {
        return queryFactory
                .select(notification.count())
                .from(notification)
                .fetchOne();
    }
}