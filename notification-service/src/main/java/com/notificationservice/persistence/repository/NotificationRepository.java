package com.notificationservice.persistence.repository;

import com.notificationservice.persistence.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}