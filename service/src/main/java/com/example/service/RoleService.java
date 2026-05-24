package com.example.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.dto.RoleRequestDto;
import com.example.common.dto.RoleResponseDto;
import com.example.common.enums.DatabaseOperation;
import com.example.common.exceptions.DuplicateRoleException;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.mapper.RoleMapper;
import com.example.model.Privilege;
import com.example.model.Role;
import com.example.repository.PrivilegeRepository;
import com.example.repository.RoleRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RoleService {
    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper mapper;
    private final EventPublisher eventPublisher;

    public RoleResponseDto getRoleById(Long id) {
        return roleRepository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role with id " + id + " not found"));
    }

    public List<RoleResponseDto> getAllRoles(Pageable pageable) {
        return roleRepository
                .findAll(pageable).stream()
                .map(mapper::toDto).toList();
    }

    @Transactional
    public RoleResponseDto createRole(RoleRequestDto dto) {
        if (roleRepository.existsByName(dto.name())) {
            throw new DuplicateRoleException("Role with name " + dto.name() + " already exists");
        }

        List<Privilege> privileges = getPrivileges(dto.privileges());
        Role role = mapper.toRole(dto, privileges);

        RoleResponseDto responseDto = mapper.toDto(roleRepository.save(role));
        eventPublisher.publishEvent(DatabaseOperation.CREATED, "Role", "createRole", responseDto);
        return responseDto;
    }

    @Transactional
    public RoleResponseDto updateRole(Long id, RoleRequestDto dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role with id " + id + " not found"));
        if (!role.getName().equals(dto.name()) && roleRepository.existsByName(dto.name())) {
            throw new DuplicateRoleException("Role with name " + dto.name() + " already exists");
        }
        role.setName(dto.name());
        List<Privilege> privileges = getPrivileges(dto.privileges());
        role.setPrivileges(new HashSet<>(privileges));

        RoleResponseDto responseDto = mapper.toDto(role);
        eventPublisher.publishEvent(DatabaseOperation.UPDATED, "Role", "updateRole", responseDto);

        return responseDto;
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role with id " + id + " not found"));
        RoleResponseDto dto = mapper.toDto(role);
        roleRepository.deleteById(id);
        eventPublisher.publishEvent(DatabaseOperation.DELETED, "Role", "deleteRole", dto);
    }

    private List<Privilege> getPrivileges(List<String> privilegeNames) {
        List<Privilege> existingPrivileges = privilegeRepository.findAllByNameIn(privilegeNames);

        Set<String> existingNames = existingPrivileges.stream()
                .map(Privilege::getName)
                .collect(Collectors.toSet());

        List<Privilege> newPrivileges = privilegeNames.stream()
                .filter(name -> !existingNames.contains(name))
                .map(this::toPrivilege)
                .toList();

        List<Privilege> savedPrivileges = new ArrayList<>();
        if (!newPrivileges.isEmpty()) {
            savedPrivileges = privilegeRepository.saveAll(newPrivileges);
            List<String> savedPrivilegeNames = savedPrivileges.stream()
                    .map(Privilege::getName)
                    .toList();
            eventPublisher.publishEvent(DatabaseOperation.CREATED, "Privilege", "createPrivileges",
                    savedPrivilegeNames);
        }

        List<Privilege> allPrivileges = new ArrayList<>(existingPrivileges);
        allPrivileges.addAll(savedPrivileges);

        return allPrivileges;
    }

    private Privilege toPrivilege(String name) {
        Privilege p = new Privilege();
        p.setName(name);
        return p;
    }
}
