package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.dto.RoleRequestDto;
import com.example.common.dto.RoleResponseDto;
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

    public RoleResponseDto getRoleById(Long id) {
        return roleRepository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role with id " + id + " not found"));
    }

    public List<RoleResponseDto> getAllRoles(Pageable pageable) {
        return roleRepository
                .findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort())).stream()
                .map(mapper::toDto).toList();
    }

    @Transactional
    public RoleResponseDto createRole(RoleRequestDto dto) {
        if (roleRepository.existsByName(dto.name())) {
            throw new DuplicateRoleException("Role with name " + dto.name() + " already exists");
        }

        List<Privilege> privileges = getPrivileges(dto.privileges());
        Role role = mapper.toRole(dto, privileges);

        return mapper.toDto(roleRepository.save(role));
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
        role.setPrivileges(privileges);

        return mapper.toDto(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role with id " + id + " not found");
        }
        roleRepository.deleteById(id);
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

        if (!newPrivileges.isEmpty()) {
            privilegeRepository.saveAll(newPrivileges);
        }

        List<Privilege> allPrivileges = new ArrayList<>(existingPrivileges);
        allPrivileges.addAll(newPrivileges);

        return allPrivileges;
    }

    private Privilege toPrivilege(String name) {
        Privilege p = new Privilege();
        p.setName(name);
        return p;
    }
}
