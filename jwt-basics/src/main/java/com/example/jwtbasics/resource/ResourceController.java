package com.example.jwtbasics.resource;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Complete example: Spring verifies bearer tokens before calling these methods. */
@RestController
public class ResourceController {
    @GetMapping("/api/public")
    public Map<String, String> publicInfo() {
        return Map.of("message", "No token needed");
    }

    @GetMapping("/api/me")
    public Map<String, String> me(@AuthenticationPrincipal Jwt jwt) {
        return Map.of("username", jwt.getSubject());
    }

    @GetMapping("/api/admin")
    public Map<String, String> admin(@AuthenticationPrincipal Jwt jwt) {
        return Map.of("message", "Hello, admin", "username", jwt.getSubject());
    }
}
