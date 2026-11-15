package com.example.oauthlogin.profile;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/** Student task: map GitHub attributes into an application profile. */
@Service
public class GitHubProfileService {
    public GitHubProfile from(OAuth2User user) {
        // TODO: read numeric "id", required "login", and optional "name" attributes.
        // Use login as displayName when name is missing or blank.
        // GitHub id is stable; a login name may change.
        // Never include the GitHub access token in the profile.
        throw new UnsupportedOperationException("Implement GitHub profile mapping");
    }
}
