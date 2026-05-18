package com.example.security;

import org.springframework.security.core.Authentication;

public interface IAuthFacade {
    Authentication getAuthentication();

    String getUsername();
}
