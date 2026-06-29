package com.example.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.Chat;
import com.example.model.User;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByChatCaseIdAndParticipantsUsername(Long caseId, String username);

    Optional<Chat> findByIdAndParticipantsUsername(Long chatId, String username);

    Optional<Chat> findByIdAndParticipants(Long chatId, User user);

    boolean existsByIdAndParticipants(Long chatId, User user);
}
