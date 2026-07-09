package com.example.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.opentmf.commons.patch.JsonMergePatch;
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
import com.example.common.exceptions.UnprocessableContentException;
import com.example.mapper.CaseMapper;
import com.example.mapper.UserMapper;
import com.example.model.Case;
import com.example.model.User;
import com.example.repository.CaseRepository;
import com.example.repository.UserRepository;
import com.example.repository.specification.CaseSpecification;
import com.example.security.IAuthFacade;
import lombok.AllArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

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
    private final ObjectMapper objectMapper;

    public CaseResponseDto getCaseById(Long id) {
        String username = authFacade.getUsername();
        Case caseEntity = repository.findByIdAndAssignedUsersUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Case with id " + id + " not found"));

        return mapper.toDto(caseEntity);
    }

    public Page<CaseResponseDto> getCases(String searchTerm, List<CaseType> types, List<CaseStatus> statuses,
            Instant minCreatedAt, Instant maxCreatedAt, Pageable pageable) {
        String username = authFacade.getUsername();
        Specification<Case> spec = CaseSpecification.hasUser(username);

        if (searchTerm != null && !searchTerm.isBlank()) {
            spec = spec.and(CaseSpecification.hasSearchTerm(searchTerm));
        }
        if (types != null && !types.isEmpty()) {
            spec = spec.and(CaseSpecification.hasAnyType(types));
        }
        if (statuses != null && !statuses.isEmpty()) {
            spec = spec.and(CaseSpecification.hasAnyStatus(statuses));
        }
        if (minCreatedAt != null) {
            spec = spec.and(CaseSpecification.isCreatedAfter(minCreatedAt));
        }
        if (maxCreatedAt != null) {
            spec = spec.and(CaseSpecification.isCreatedBefore(maxCreatedAt));
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

        if (assignedUsers.size() < dto.assignedUserIds().size()) {
            throw new UnprocessableContentException("One or more users not found");
        }

        Case savedCase = repository.save(mapper.toCase(dto, assignedUsers, new ArrayList<>()));
        CaseResponseDto responseDto = mapper.toDto(savedCase);
        eventPublisher.publishEvent(DatabaseOperation.CREATED, "Case", "createCase", responseDto);
        publishCaseNotification(dto.name(), assignedUsers, username);

        return responseDto;
    }

    private void publishCaseNotification(String caseName, List<User> assignedUsers, String username) {
        List<String> recipients =
                assignedUsers.stream().map((u) -> u.getUsername()).filter(u -> !u.equals(username)).toList();

        userNotificationPublisher
                .publishUserNotification("New case assigned", "You have been assigned to " + caseName, recipients);

    }

    public Long getCaseCount(String searchTerm, List<CaseType> types, List<CaseStatus> statuses,
            Instant minCreatedAt, Instant maxCreatedAt) {
        String username = authFacade.getUsername();
        Specification<Case> spec = CaseSpecification.hasUser(username);

        if (searchTerm != null && !searchTerm.isBlank()) {
            spec = spec.and(CaseSpecification.hasSearchTerm(searchTerm));
        }
        if (types != null && !types.isEmpty()) {
            spec = spec.and(CaseSpecification.hasAnyType(types));
        }
        if (statuses != null && !statuses.isEmpty()) {
            spec = spec.and(CaseSpecification.hasAnyStatus(statuses));
        }
        if (minCreatedAt != null) {
            spec = spec.and(CaseSpecification.isCreatedAfter(minCreatedAt));
        }
        if (maxCreatedAt != null) {
            spec = spec.and(CaseSpecification.isCreatedBefore(maxCreatedAt));
        }

        return repository.count(spec);
    }

    @Transactional
    public CaseResponseDto partiallyUpdateCase(Long id, JsonNode patchNode) {
        Case existingCase = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case with id " + id + " not found"));

        Set<String> originalUsernames = existingCase.getAssignedUsers().stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());

        CaseRequestDto existingDto = mapper.toRequestDto(existingCase);
        JsonNode targetNode = objectMapper.valueToTree(existingDto);
        JsonMergePatch mergePatch = JsonMergePatch.of(patchNode);
        JsonNode patchedNode = mergePatch.apply(targetNode);
        CaseRequestDto patchedCaseDto = objectMapper.treeToValue(patchedNode, CaseRequestDto.class);

        Set<Long> uniqueRequestedUserIds = new HashSet<>(patchedCaseDto.assignedUserIds());
        List<User> assignedUsers = userRepository.findAllById(uniqueRequestedUserIds);

        if (assignedUsers.size() < uniqueRequestedUserIds.size()) {
            throw new UnprocessableContentException("One or more users not found");
        }

        mapper.updateCaseFromDto(existingCase, patchedCaseDto, assignedUsers);

        List<String> newlyAssignedUsernames = assignedUsers.stream()
                .map(User::getUsername)
                .filter(username -> !originalUsernames.contains(username))
                .toList();

        if (!newlyAssignedUsernames.isEmpty()) {
            userNotificationPublisher.publishUserNotification(
                    "New case assigned",
                    "You have been assigned to " + existingCase.getName(),
                    newlyAssignedUsernames);
        }

        CaseResponseDto responseDto = mapper.toDto(existingCase);
        eventPublisher.publishEvent(DatabaseOperation.UPDATED, "Case", "partiallyUpdateCase", responseDto);

        return responseDto;
    }
}
