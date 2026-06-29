package com.example.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.example.model.UserNotification;


public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    List<UserNotification> findByRecipient(String recipient, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserNotification n SET n.isRead = true WHERE n.recipient = :username")
    void markAllUserNotificationsAsRead(String username);
}
