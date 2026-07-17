package com.example.enterprise_project_management_platform.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSessionResponse {

    private String sessionId;

    private String deviceName;

    private String ipAddress;

    private String userAgent;

    private LocalDateTime loginTime;

    private LocalDateTime lastActive;

    private boolean active;
}
