package com.example.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.dto.JwtResponseDto;
import com.example.common.dto.LoginRequestDto;
import com.example.common.dto.RegisterRequestDto;
import com.example.common.dto.UserRequestDto;
import com.example.common.dto.UserResponseDto;
import com.example.common.enums.DatabaseOperation;
import com.example.common.exceptions.DuplicateUsernameException;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.common.exceptions.UnprocessableContentException;
import com.example.mapper.UserMapper;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.example.security.IAuthFacade;
import com.example.security.JwtUtils;
import com.example.security.MyUserDetails;
import com.example.security.MyUserDetailsService;
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
    private final EventPublisher eventPublisher;
    private final IAuthFacade authFacade;
    private final MyUserDetailsService userDetailsService;

    public UserResponseDto getCurrentUser(MyUserDetails userDetails) {
        if (userDetails == null) {
            String username = authFacade.getUsername();
            userDetails = userDetailsService.loadUserByUsername(username);
        }
        List<String> roles = userDetails.getAuthorities().stream().map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_"))
                .toList();
        return new UserResponseDto(userDetails.getId(), userDetails.getUsername(), roles);
    }

    @Transactional
    public JwtResponseDto registerUser(RegisterRequestDto dto) throws DuplicateUsernameException {

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
    public JwtResponseDto authenticate(LoginRequestDto dto) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));
        return jwtUtils.generateJwtToken(authentication);
    }

    public UserResponseDto getUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return mapper.toDto(user);
    }

    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {
        if (repository.existsByUsername(dto.username())) {
            throw new DuplicateUsernameException("Account with username " + dto.username() + " already exists");
        }

        List<Role> roles = roleRepository.findAllByNameIn(dto.roleNames());
        if (roles.size() != dto.roleNames().size()) {
            throw new UnprocessableContentException("One or more roles not found");
        }
        String encodedPassword = passwordEncoder.encode(dto.password());

        User user = mapper.toUser(dto, encodedPassword, roles);
        UserResponseDto responseDto = mapper.toDto(repository.save(user));

        eventPublisher.publishEvent(DatabaseOperation.CREATED, "User", "createUser", responseDto);
        return responseDto;
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto dto) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with id " + id + " not found"));

        if (!user.getUsername().equals(dto.username()) && repository.existsByUsername(dto.username())) {
            throw new DuplicateUsernameException("Account with username " + dto.username() + " already exists");
        }

        Set<Role> roles = new HashSet<>(roleRepository.findAllByNameIn(dto.roleNames()));
        if (roles.size() != dto.roleNames().size()) {
            throw new UnprocessableContentException("One or more roles not found");
        }

        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRoles(roles);

        UserResponseDto responseDto = mapper.toDto(user);
        eventPublisher.publishEvent(DatabaseOperation.UPDATED, "User", "updateUser", responseDto);
        return responseDto;
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with id " + id + " not found"));
        UserResponseDto dto = mapper.toDto(user);
        eventPublisher.publishEvent(DatabaseOperation.DELETED, "User", "deleteUser", dto);
        repository.deleteById(id);
    }
}
