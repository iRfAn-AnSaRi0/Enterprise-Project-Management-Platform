package com.example.enterprise_project_management_platform.service;

import org.springframework.security.core.Authentication;

import com.example.enterprise_project_management_platform.dto.UserResponse;

public interface UserService {

   UserResponse getCurrentUser(
        Authentication authentication
);
} 