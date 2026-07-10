package com.example.enterprise_project_management_platform.service;

public interface JwtService {

    String generateAccessToken(String email);

    String extractEmail(String token);

    boolean isValid(String token);

}
