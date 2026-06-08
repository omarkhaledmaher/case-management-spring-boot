package com.example.web;

import java.util.Collections;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.model.Privilege;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.PrivilegeRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Profile("dev")
@Component
@AllArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Privilege caseCreatePrivilege = new Privilege(null, "CASE_CREATE", Collections.emptySet());
        privilegeRepository.save(caseCreatePrivilege);

        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole.setPrivileges(Set.of(caseCreatePrivilege));
        roleRepository.save(adminRole);

        Role userRole = new Role();
        userRole.setName("USER");
        roleRepository.save(userRole);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRoles(Set.of(adminRole));

        userRepository.save(admin);
    }
}
