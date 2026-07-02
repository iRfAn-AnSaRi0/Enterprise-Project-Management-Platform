package com.example.enterprise_project_management_platform.service;

public interface EmailService {

    void sendVerificationEmail(String toEmail, String userName, String token);
} 
