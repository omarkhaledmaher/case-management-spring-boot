package com.example.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.model.Case;

public interface CaseRepository extends JpaRepository<Case, Long> {
    Page<Case> findByAssignedUsersUsername(String username, Pageable pageable);

    Optional<Case> findByIdAndAssignedUsersUsername(Long caseId, String username);

    @Query("SELECT DISTINCT c FROM Case c JOIN c.assignedUsers u WHERE " +
            "u.username = :username AND (" +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.details.customerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.details.applicantName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.details.referenceName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
            ")")
    Page<Case> search(@Param("searchTerm") String searchTerm,
            @Param("username") String username, Pageable pageable);
}
