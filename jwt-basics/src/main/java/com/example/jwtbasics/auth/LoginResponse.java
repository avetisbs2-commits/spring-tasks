package com.example.jwtbasics.auth;

public record LoginResponse(String tokenType, String accessToken, long expiresIn) {}
