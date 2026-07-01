package com.example.mapper;

import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Component;
import com.example.common.dto.ChatParticipantResponseDto;
import com.example.common.dto.RegisterRequestDto;
import com.example.common.dto.RoleResponseDto;
import com.example.common.dto.UserRequestDto;
import com.example.common.dto.UserResponseDto;
import com.example.model.Role;
import com.example.model.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final RoleMapper roleMapper;

    public UserResponseDto toDto(User user) {
        return new UserResponseDto(user.getId(),
                user.getUsername(),
                user.getRoles().stream().map(roleMapper::toDto).map(RoleResponseDto::name).toList());
    }

    public User toUser(UserRequestDto dto, String encodedPassword, List<Role> roles) {
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(encodedPassword);
        user.setRoles(new HashSet<>(roles));
        return user;
    }

    public User toUser(RegisterRequestDto dto, String encodedPassword, List<Role> roles) {
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(encodedPassword);
        user.setRoles(new HashSet<>(roles));
        return user;
    }

    public ChatParticipantResponseDto toChatParticipantDto(User user) {
        return new ChatParticipantResponseDto(user.getId(), user.getUsername());
    }
}
