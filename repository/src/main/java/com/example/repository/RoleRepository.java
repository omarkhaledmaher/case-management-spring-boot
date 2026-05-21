package com.example.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT r.id FROM Role r")
    Page<Long> findAllIds(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "privileges")
    List<Role> findAllById(Iterable<Long> ids);

    Role findByName(String name);

    List<Role> findAllByNameIn(List<String> names);

    boolean existsByName(String name);
}
