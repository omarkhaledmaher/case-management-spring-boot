package com.example.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.model.Chat;
import com.example.model.User;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @EntityGraph(attributePaths = {"participants", "messages"})
    @Override
    List<Chat> findAllById(Iterable<Long> ids);

    @Query("select c.id from Chat c join c.participants p " +
            "where c.chatCase.id = :caseId and p.username = :username")
    Page<Long> findChatIdsByChatCaseIdAndParticipantsUsername(@Param("caseId") Long caseId,
            @Param("username") String username, Pageable pageable);

    Optional<Chat> findByIdAndParticipantsUsername(Long chatId, String username);

    Optional<Chat> findByIdAndParticipants(Long chatId, User user);

}
