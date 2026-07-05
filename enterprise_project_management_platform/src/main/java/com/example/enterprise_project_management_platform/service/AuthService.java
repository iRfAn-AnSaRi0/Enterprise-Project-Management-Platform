package com.example.enterprise_project_management_platform.service;

import com.example.enterprise_project_management_platform.dto.RegisterRequest;
import com.example.enterprise_project_management_platform.dto.RegisterResponse;

public interface AuthService {
    
    RegisterResponse register(RegisterRequest request);

    String verifyEmail(String token);
}
