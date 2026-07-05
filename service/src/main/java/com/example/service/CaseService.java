package com.example.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.dto.CaseRequestDto;
import com.example.common.dto.CaseResponseDto;
import com.example.common.dto.ChatParticipantResponseDto;
import com.example.common.enums.CaseStatus;
import com.example.common.enums.CaseType;
import com.example.common.enums.DatabaseOperation;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.mapper.CaseMapper;
import com.example.mapper.UserMapper;
import com.example.model.Case;
import com.example.model.User;
import com.example.repository.CaseRepository;
import com.example.repository.UserRepository;
import com.example.repository.specification.CaseSpecification;
import com.example.security.IAuthFacade;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CaseService {
    private final CaseMapper mapper;
    private final CaseRepository repository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EventPublisher eventPublisher;
    private final UserNotificationPublisher userNotificationPublisher;
    private final IAuthFacade authFacade;

    public CaseResponseDto getCaseById(Long id) {
        String username = authFacade.getUsername();
        Case caseEntity = repository.findByIdAndAssignedUsersUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Case with id " + id + " not found"));

        return mapper.toDto(caseEntity);
    }

    public Page<CaseResponseDto> getCases(String searchTerm, CaseType type, CaseStatus status, Pageable pageable) {
        String username = authFacade.getUsername();
        Specification<Case> spec = CaseSpecification.hasUser(username);

        if (searchTerm != null && !searchTerm.isBlank()) {
            spec = spec.and(CaseSpecification.hasSearchTerm(searchTerm));
        }
        if (type != null) {
            spec = spec.and(CaseSpecification.hasType(type));
        }
        if (status != null) {
            spec = spec.and(CaseSpecification.hasStatus(status));
        }

        Page<Case> cases = repository.findAll(spec, pageable);
        return cases.map(mapper::toDto);
    }


    public Page<ChatParticipantResponseDto> getUsersByCaseId(Long id, Pageable pageable) {
        String username = authFacade.getUsername();
        if (!repository.existsByIdAndAssignedUsersUsername(id, username)) {
            throw new ResourceNotFoundException("Case with id " + id + " not found");
        }
        return userRepository.findByAssignedCasesId(id, pageable).map(userMapper::toChatParticipantDto);
    }

    @Transactional
    public CaseResponseDto createCase(CaseRequestDto dto) {
        String username = authFacade.getUsername();

        List<User> assignedUsers = userRepository.findAllById(dto.assignedUserIds());
        Case savedCase = repository.save(mapper.toCase(dto, assignedUsers, new ArrayList<>()));
        repository.flush();
        CaseResponseDto responseDto = mapper.toDto(savedCase);
        eventPublisher.publishEvent(DatabaseOperation.CREATED, "Case", "createCase", responseDto);
        publishCaseNotification(assignedUsers, username);

        return responseDto;
    }

    private void publishCaseNotification(List<User> assignedUsers, String username) {
        assignedUsers.stream()
                .filter(assignedUser -> !assignedUser.getUsername().equals(username))
                .forEach(assignedUser -> userNotificationPublisher.publishUserNotification(
                        "New case assigned",
                        "You have been assigned to a new case.",
                        assignedUser.getUsername()));

    }
}
