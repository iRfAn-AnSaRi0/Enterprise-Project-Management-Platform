package com.example.enterprise_project_management_platform.service.implement;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.enterprise_project_management_platform.dto.UserSessionResponse;
import com.example.enterprise_project_management_platform.entity.UserEntity;
import com.example.enterprise_project_management_platform.entity.UserSessionEntity;
import com.example.enterprise_project_management_platform.repository.UserRepository;
import com.example.enterprise_project_management_platform.repository.UserSessionRepository;
import com.example.enterprise_project_management_platform.service.UserSessionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserSessionServiceImple implements UserSessionService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;

    @Override
    public List<UserSessionResponse> getCurrentUserSessions() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        UserEntity user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException("User not found"));

        return userSessionRepository
                .findByUserAndActiveTrue(user)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    private UserSessionResponse convertToDto(
            UserSessionEntity session) {

        UserSessionResponse dto =
                new UserSessionResponse();

        dto.setSessionId(session.getSessionId());
        dto.setDeviceName(session.getDeviceName());
        dto.setIpAddress(session.getIpAddress());
        dto.setUserAgent(session.getUserAgent());
        dto.setLoginTime(session.getLoginTime());
        dto.setLastActive(session.getLastActive());
        dto.setActive(session.isActive());

        return dto;
    }

    @Override
    public void logoutSession(String sessionId) {

        UserSessionEntity session =
                userSessionRepository
                        .findBySessionId(sessionId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Session not found"));

        session.setActive(false);

        userSessionRepository.save(session);
    }

    @Override
    public void logoutAllSessions() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        UserEntity user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException("User not found"));

        List<UserSessionEntity> sessions =
                userSessionRepository
                        .findByUserAndActiveTrue(user);

        sessions.forEach(
                session -> session.setActive(false));

        userSessionRepository.saveAll(sessions);
    }
}
