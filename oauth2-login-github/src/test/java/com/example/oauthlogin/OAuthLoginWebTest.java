package com.example.oauthlogin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/** Simulates GitHub's user data; no external credentials or network needed. */
@SpringBootTest
@AutoConfigureMockMvc
class OAuthLoginWebTest {
    @Autowired MockMvc mockMvc;

    @Test
    void homeIsPublicAndOffersGitHubLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "/oauth2/authorization/github")));
    }

    @Test
    void authorizationEndpointRedirectsToGitHub() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/github"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith(
                        "https://github.com/login/oauth/authorize?")))
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("state=")));
    }

    @Test
    void anonymousProfileStartsLogin() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/oauth2/authorization/github"));
    }

    @Test
    void signedInUserSeesMappedProfile() throws Exception {
        mockMvc.perform(get("/profile").with(oauth2Login().attributes(attributes -> {
                    attributes.put("id", 12345);
                    attributes.put("login", "octocat");
                    attributes.put("name", "The Octocat");
                })))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("octocat")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("The Octocat")));
    }
}
