package com.example.oauthlogin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/** Student task: enable the OAuth2 authorization-code login flow. */
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/error", "/css/**").permitAll()
                        .anyRequest().authenticated())
                .logout(logout -> logout.logoutSuccessUrl("/"));

        // TODO: enable oauth2Login and send successful logins to /profile.
        // Keep CSRF enabled: this browser app uses a session and a POST logout form.
        // Hint: http.oauth2Login(oauth -> oauth.defaultSuccessUrl("/profile", true));

        return http.build();
    }
}
