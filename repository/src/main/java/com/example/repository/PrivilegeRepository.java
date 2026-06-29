package com.example.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    List<Privilege> findAllByNameIn(List<String> privileges);
}
