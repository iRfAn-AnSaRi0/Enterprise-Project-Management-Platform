package com.example.enterprise_project_management_platform.service;

import java.util.List;

import com.example.enterprise_project_management_platform.dto.UserSessionResponse;

public interface UserSessionService {

    List<UserSessionResponse> getCurrentUserSessions();

    void logoutSession(String sessionId);

    void logoutAllSessions();
}