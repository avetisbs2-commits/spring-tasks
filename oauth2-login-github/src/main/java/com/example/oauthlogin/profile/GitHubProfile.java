package com.example.oauthlogin.profile;

/** Only the profile fields this app needs; never expose the provider access token. */
public record GitHubProfile(Long githubId, String login, String displayName) {}
