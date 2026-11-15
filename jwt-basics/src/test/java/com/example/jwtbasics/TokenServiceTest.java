package com.example.jwtbasics;

import com.example.jwtbasics.auth.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** Student stage 1: issue a signed token with the expected claims. */
@SpringBootTest
class TokenServiceTest {
    @Autowired TokenService tokenService;
    @Autowired JwtDecoder decoder;

    @Test
    void issuedTokenHasSubjectRoleIssuerAndShortExpiry() {
        String value = tokenService.issue("student", List.of("USER"));
        Jwt jwt = decoder.decode(value);
        assertEquals("student", jwt.getSubject());
        assertEquals("http://localhost:8085", jwt.getIssuer().toString());
        assertEquals(List.of("USER"), jwt.getClaimAsStringList("roles"));
        assertEquals(900, Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt()).toSeconds());
        assertFalse(jwt.getClaims().containsKey("password"));
    }
}
