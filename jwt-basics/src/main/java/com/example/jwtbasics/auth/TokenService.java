package com.example.jwtbasics.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;


/** Student task: create short-lived, signed access tokens. */
@Service
public class TokenService {
    private final JwtEncoder encoder;
    private final Clock clock;
    private final String issuer;
    private final long ttlSeconds;

    public TokenService(JwtEncoder encoder, Clock clock,
                        @Value("${jwt.issuer}") String issuer,
                        @Value("${jwt.ttl-seconds}") long ttlSeconds) {
        this.encoder = encoder;
        this.clock = clock;
        this.issuer = issuer;
        this.ttlSeconds = ttlSeconds;
    }

    public String issue(String username, List<String> roles) {
        // TODO: build JwtClaimsSet with issuer, subject=username, issuedAt=clock.instant(),
        // expiresAt=now.plusSeconds(ttlSeconds), and claim "roles" from the authenticated
        // user's authorities. Sign with encoder using JwsHeader HS256. Return token value.
        Instant now = clock.instant();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(username)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ttlSeconds))
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(header, claims);

        Jwt token = encoder.encode(parameters);
        return token.getTokenValue();
    }

    public long ttlSeconds() {
        return ttlSeconds;
    }
}
