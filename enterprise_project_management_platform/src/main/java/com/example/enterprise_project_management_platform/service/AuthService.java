package com.example.enterprise_project_management_platform.service;

import com.example.enterprise_project_management_platform.dto.LoginRequest;
import com.example.enterprise_project_management_platform.dto.LoginResponse;
import com.example.enterprise_project_management_platform.dto.RefreshTokenRequest;
import com.example.enterprise_project_management_platform.dto.RefreshTokenResponse;
import com.example.enterprise_project_management_platform.dto.RegisterRequest;
import com.example.enterprise_project_management_platform.dto.RegisterResponse;

public interface AuthService {
    
    RegisterResponse register(RegisterRequest request);

    String verifyEmail(String token);

    LoginResponse login(LoginRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
}
