package com.example.oauthlogin;

import com.example.oauthlogin.profile.GitHubProfile;
import com.example.oauthlogin.profile.GitHubProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Student task: turn provider attributes into a small app profile. */
class GitHubProfileServiceTest {
    private final GitHubProfileService service = new GitHubProfileService();

    private OAuth2User user(Map<String, Object> attributes) {
        return new DefaultOAuth2User(Set.of(new SimpleGrantedAuthority("ROLE_USER")),
                attributes, "id");
    }

    @Test
    void mapsStableIdLoginAndDisplayName() {
        GitHubProfile profile = service.from(user(Map.of(
                "id", 12345, "login", "octocat", "name", "The Octocat")));
        assertEquals(12345L, profile.githubId());
        assertEquals("octocat", profile.login());
        assertEquals("The Octocat", profile.displayName());
    }

    @Test
    void usesLoginWhenOptionalNameIsMissing() {
        GitHubProfile profile = service.from(user(Map.of("id", 12345L, "login", "octocat")));
        assertEquals("octocat", profile.displayName());
    }
}
