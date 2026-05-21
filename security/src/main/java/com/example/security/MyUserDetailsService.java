package com.example.security;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.model.Privilege;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public MyUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).map(toUserDetails())
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));
    }

    private List<? extends GrantedAuthority> getAuthorities(List<Role> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(List<Role> roles) {

        var privileges = new ArrayList<String>();
        var collection = new ArrayList<Privilege>();
        for (Role role : roles) {
            privileges.add("ROLE_" + role.getName().toString());
            collection.addAll(role.getPrivileges());
        }
        for (Privilege item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        var authorities = new ArrayList<GrantedAuthority>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    private Function<User, MyUserDetails> toUserDetails() {
        return user -> new MyUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                getAuthorities(user.getRoles()));
    }
}
