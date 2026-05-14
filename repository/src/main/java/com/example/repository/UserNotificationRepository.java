package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.UserNotification;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

}
