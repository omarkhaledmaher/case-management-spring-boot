package com.example.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.dto.CaseRequestDto;
import com.example.common.dto.CaseResponseDto;
import com.example.common.enums.DatabaseOperation;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.mapper.CaseMapper;
import com.example.model.Case;
import com.example.model.User;
import com.example.repository.CaseRepository;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CaseService {
    private final CaseMapper mapper;
    private final CaseRepository repository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public CaseResponseDto getCaseById(Long id, String username) {
        Case caseEntity = repository.findByIdAndAssignedUsersUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Case with id " + id + " not found"));

        return mapper.toDto(caseEntity);
    }

    public List<CaseResponseDto> getAllCases(String username, Pageable pageable) {
        List<Case> cases = repository.findByAssignedUsersUsername(username, pageable);
        return cases.stream().map(mapper::toDto).toList();
    }

    @Transactional
    public CaseResponseDto createCase(CaseRequestDto dto, String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new UsernameNotFoundException("User with username " + username + " not found");
        }

        List<User> assignedUsers = userRepository.findAllById(dto.assignedUserIds());
        Case savedCase = repository.save(mapper.toCase(dto, assignedUsers, new ArrayList<>()));
        repository.flush();
        CaseResponseDto responseDto = mapper.toDto(savedCase);
        eventPublisher.publishEvent(DatabaseOperation.CREATED, "Case", "createCase", username, responseDto);
        return responseDto;
    }
}
