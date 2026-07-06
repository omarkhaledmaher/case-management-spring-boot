package com.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.example.model.UserNotification;


public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    Page<UserNotification> findByRecipient(String recipient, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserNotification n SET n.isRead = true WHERE n.recipient = :username")
    void markAllUserNotificationsAsRead(String username);

    Long countByRecipientAndIsRead(String username, boolean b);
}
