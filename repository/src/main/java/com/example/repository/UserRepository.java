package com.example.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"roles", "roles.privileges"})
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Page<User> findByAssignedCasesId(Long caseId, Pageable pageable);
}
