package com.example.mapper;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.common.dto.ChatParticipantDto;
import com.example.common.dto.RegisterDto;
import com.example.common.dto.RoleResponseDto;
import com.example.common.dto.UserRequestDto;
import com.example.common.dto.UserResponseDto;
import com.example.model.Role;
import com.example.model.User;

@Component
public class UserMapper {
    @Autowired
    private RoleMapper roleMapper;

    public UserResponseDto toDto(User user) {
        return new UserResponseDto(user.getId(),
                user.getUsername(),
                user.getRoles().stream().map(roleMapper::toDto).map(RoleResponseDto::name).toList());
    }

    public User toUser(UserRequestDto dto, String encodedPassword, List<Role> roles) {
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(encodedPassword);
        user.setRoles(roles);
        return user;
    }

    public User toUser(RegisterDto dto, String encodedPassword, List<Role> roles) {
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(encodedPassword);
        user.setRoles(roles);
        return user;
    }

    public ChatParticipantDto toChatParticipantDto(User user) {
        return new ChatParticipantDto(user.getId(), user.getUsername());
    }
}
