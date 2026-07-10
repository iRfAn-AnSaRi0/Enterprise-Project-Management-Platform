package com.example.enterprise_project_management_platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class JwtConfig {
    
     @Value("${jwt.secret}")
     private String secret;

     @Value("${jwt.access-token-expiration}")
     private long accessTokenExpiration;

     @Value("${jwt.refresh-token-expiration}")
     private long refreshTokenExpiration;

}
