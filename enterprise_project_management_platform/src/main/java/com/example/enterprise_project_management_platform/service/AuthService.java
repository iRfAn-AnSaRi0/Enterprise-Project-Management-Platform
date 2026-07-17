package com.example.enterprise_project_management_platform.service;

import com.example.enterprise_project_management_platform.dto.ForgotPasswordRequest;
import com.example.enterprise_project_management_platform.dto.LoginRequest;
import com.example.enterprise_project_management_platform.dto.LoginResponse;
import com.example.enterprise_project_management_platform.dto.LogoutRequest;
import com.example.enterprise_project_management_platform.dto.RefreshTokenRequest;
import com.example.enterprise_project_management_platform.dto.RefreshTokenResponse;
import com.example.enterprise_project_management_platform.dto.RegisterRequest;
import com.example.enterprise_project_management_platform.dto.RegisterResponse;
import com.example.enterprise_project_management_platform.dto.ResetPasswordRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    String verifyEmail(String token);

    LoginResponse login(LoginRequest request, HttpServletRequest httpServletRequest);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout(LogoutRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
