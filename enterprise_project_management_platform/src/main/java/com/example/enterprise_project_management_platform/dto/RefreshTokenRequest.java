package com.example.enterprise_project_management_platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    
     @NotBlank
     private String refreshToken;

}
