package com.example.jwtbasics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** These tests should pass before the login TODOs are implemented. */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityBoundaryTest {
    @Autowired MockMvc mockMvc;
    @Autowired JwtEncoder encoder;

    private String signedToken(String issuer, Instant expiresAt, List<String> roles) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject("student")
                .issuedAt(expiresAt.minusSeconds(300))
                .expiresAt(expiresAt)
                .claim("roles", roles)
                .build();
        return encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }

    @Test
    void publicIsOpenButProtectedRouteNeedsToken() throws Exception {
        mockMvc.perform(get("/api/public"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signedTokenIdentifiesUserAndRoleControlsAdmin() throws Exception {
        String userToken = signedToken("http://localhost:8085", Instant.now().plusSeconds(300), List.of("USER"));
        mockMvc.perform(get("/api/me").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("student"));
        mockMvc.perform(get("/api/admin").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        String adminToken = signedToken("http://localhost:8085", Instant.now().plusSeconds(300), List.of("ADMIN"));
        mockMvc.perform(get("/api/admin").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void expiredWrongIssuerAndModifiedSignatureAreRejected() throws Exception {
        String expired = signedToken("http://localhost:8085", Instant.now().minusSeconds(120), List.of("USER"));
        String wrongIssuer = signedToken("https://other.example", Instant.now().plusSeconds(300), List.of("USER"));
        String good = signedToken("http://localhost:8085", Instant.now().plusSeconds(300), List.of("USER"));
        int signatureStart = good.lastIndexOf('.') + 1;
        char replacement = good.charAt(signatureStart) == 'A' ? 'B' : 'A';
        String tampered = good.substring(0, signatureStart) + replacement + good.substring(signatureStart + 1);

        for (String token : List.of(expired, wrongIssuer, tampered)) {
            mockMvc.perform(get("/api/me").header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized());
        }
    }
}
