package com.notificationservice.persistence.repository;

import com.notificationservice.persistence.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("select n.id from Notification n where n.receiverId = :userId and n.isChecked = true")
    List<Long> findIdsByUserIdAndAlreadyChecked(@Param("userId") String userId);

    @Modifying(clearAutomatically = true)
    @Query("delete Notification n where n.id in :ids")
    void deleteInIds(@Param("ids") List<Long> ids);
}