package com.example.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.example.model.Case;

public interface CaseRepository extends JpaRepository<Case, Long>, JpaSpecificationExecutor<Case> {
    Optional<Case> findByIdAndAssignedUsersUsername(Long caseId, String username);
}
