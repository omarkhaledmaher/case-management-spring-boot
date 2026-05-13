package com.example.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import com.example.common.dto.CaseRequestDto;
import com.example.common.dto.CaseResponseDto;
import com.example.model.Case;
import com.example.model.Chat;
import com.example.model.User;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CaseMapper {
    public CaseResponseDto toDto(Case caseEntity) {
        CaseResponseDto dto = new CaseResponseDto(caseEntity.getId(), caseEntity.getType(), caseEntity.getDescription(),
                caseEntity.getCreatedAt(), caseEntity.getUpdatedAt(), caseEntity.getStatus());
        return dto;
    }

    public Case toCase(CaseRequestDto dto, List<User> assignedUsers, List<Chat> chats) {
        Case caseEntity = new Case();
        caseEntity.setType(dto.type());
        caseEntity.setDescription(dto.description());
        caseEntity.setStatus(dto.status());
        caseEntity.setAssignedUsers(assignedUsers);
        caseEntity.setChats(chats);
        return caseEntity;
    }
}
