package com.example.enterprise_project_management_platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequest {
    
    @NotBlank
    private String refreshToken;

}
