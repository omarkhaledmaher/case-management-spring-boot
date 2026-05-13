package com.example.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.model.Chat;
import com.example.model.User;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByChatCaseIdAndParticipantsUsername(Long caseId, String username, Pageable pageable);

    Optional<Chat> findByIdAndParticipantsUsername(Long chatId, String username);

    Optional<Chat> findByIdAndParticipants(Long chatId, User user);

}
