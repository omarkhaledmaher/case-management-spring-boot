package com.example.service;

import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.dto.JwtDto;
import com.example.common.dto.LoginDto;
import com.example.common.dto.RegisterDto;
import com.example.common.dto.UserRequestDto;
import com.example.common.dto.UserResponseDto;
import com.example.common.exceptions.DuplicateUsernameException;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.mapper.UserMapper;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.example.security.JwtUtils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public JwtDto registerUser(RegisterDto dto) throws DuplicateUsernameException {

        if (repository.existsByUsername(dto.username())) {
            throw new DuplicateUsernameException("Account with username " + dto.username() + " already exists");
        }
        String encodedPassword = passwordEncoder.encode(dto.password());
        List<Role> roles = roleRepository.findAllByNameIn(Arrays.asList("USER"));
        User user = mapper.toUser(dto, encodedPassword, roles);
        repository.save(user);

        return jwtUtils.generateJwtToken(user);
    }

    @Transactional
    public JwtDto authenticate(LoginDto dto) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));
        return jwtUtils.generateJwtToken(authentication);
    }

    public UserResponseDto getUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return mapper.toDto(user);
    }

    public List<UserResponseDto> getAllUsers(Pageable pageable) {
        return repository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()))
                .map(mapper::toDto).toList();
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {
        if (repository.existsByUsername(dto.username())) {
            throw new DuplicateUsernameException("Account with username " + dto.username() + " already exists");
        }

        List<Role> roles = roleRepository.findAllByNameIn(dto.roleNames());
        if (roles.size() != dto.roleNames().size()) {
            throw new ResourceNotFoundException("One or more roles not found");
        }
        String encodedPassword = passwordEncoder.encode(dto.password());

        User user = mapper.toUser(dto, encodedPassword, roles);

        return mapper.toDto(repository.save(user));
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto dto) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        List<Role> roles = roleRepository.findAllByNameIn(dto.roleNames());
        if (roles.size() != dto.roleNames().size()) {
            throw new ResourceNotFoundException("One or more roles not found");
        }

        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRoles(roles);
        return mapper.toDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        repository.deleteById(id);
    }
}
