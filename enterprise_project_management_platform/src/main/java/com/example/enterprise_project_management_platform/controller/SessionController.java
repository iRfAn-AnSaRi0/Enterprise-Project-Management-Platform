package com.example.enterprise_project_management_platform.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.enterprise_project_management_platform.dto.UserSessionResponse;
import com.example.enterprise_project_management_platform.service.UserSessionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final UserSessionService userSessionService;

    @GetMapping
    public List<UserSessionResponse> getSessions() {
        return userSessionService.getCurrentUserSessions();
    }

    @PostMapping("/{sessionId}/logout")
    public String logoutSession(
            @PathVariable String sessionId) {

        userSessionService.logoutSession(sessionId);

        return "Session logged out.";
    }

    @PostMapping("/logout-all")
    public String logoutAll() {

        userSessionService.logoutAllSessions();

        return "All sessions logged out.";
    }
}