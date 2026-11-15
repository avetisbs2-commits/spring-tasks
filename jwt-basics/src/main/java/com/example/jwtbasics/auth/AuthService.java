package com.example.jwtbasics.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

/** Student task: authenticate first, issue a token only on success. */
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest request) {
        // TODO: authenticate with UsernamePasswordAuthenticationToken(username, password).
        // Read the authenticated principal name and ROLE_ authorities from the result.
        // Strip ROLE_ when passing roles to TokenService.issue ("USER", "ADMIN").
        // Return LoginResponse("Bearer", token, tokenService.ttlSeconds()).
        // AuthenticationException is handled as 401 by AuthExceptionHandler.
        throw new UnsupportedOperationException("Implement login");
    }
}
