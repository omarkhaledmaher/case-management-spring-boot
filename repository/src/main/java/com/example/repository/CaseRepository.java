package com.example.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.model.Case;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {
    List<Case> findByAssignedUsersUsername(String username, Pageable pageable);

    Optional<Case> findByIdAndAssignedUsersUsername(Long caseId, String username);

}
