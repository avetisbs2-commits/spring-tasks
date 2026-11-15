package com.example.oauthlogin.profile;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Student task: show data from the authenticated GitHub user. */
@Controller
public class ProfileController {
    private final GitHubProfileService profileService;

    public ProfileController(GitHubProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal OAuth2User user, Model model) {
        // TODO: add the mapped GitHubProfile to the model as "profile".
        // Then return "profile" to render templates/profile.html.
        return "not-implemented";
    }
}
