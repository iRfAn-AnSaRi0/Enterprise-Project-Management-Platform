package com.example.enterprise_project_management_platform.service.implement;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.enterprise_project_management_platform.dto.RegisterRequest;
import com.example.enterprise_project_management_platform.dto.RegisterResponse;
import com.example.enterprise_project_management_platform.entity.EmailVerificationTokenEntity;
import com.example.enterprise_project_management_platform.entity.RoleEntity;
import com.example.enterprise_project_management_platform.entity.UserEntity;
import com.example.enterprise_project_management_platform.entity.UserRoleEntity;
import com.example.enterprise_project_management_platform.enums.Role;
import com.example.enterprise_project_management_platform.repository.EmailVerificationTokenRepository;
import com.example.enterprise_project_management_platform.repository.RoleRepository;
import com.example.enterprise_project_management_platform.repository.UserRepository;
import com.example.enterprise_project_management_platform.repository.UserRoleRepository;
import com.example.enterprise_project_management_platform.service.AuthService;
import com.example.enterprise_project_management_platform.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImple implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail((request.getEmail()))) { // check for existing user
            throw new RuntimeException("Email already exists");
        }

        String encodePassword = passwordEncoder.encode(request.getPassword()); // hash the password

        UserEntity user = new UserEntity(); // now save the user in DB
        UserRoleEntity userRole = new UserRoleEntity(); // save the user role and user
        RoleEntity role = roleRepository.findByName(Role.VIEWER.name())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // save user details
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(encodePassword);

        // save user role in userRole table
        userRole.setUser(user);
        userRole.setRole(role);

        userRepository.save(user);
        userRoleRepository.save(userRole);

        // Token setup for email verification
        EmailVerificationTokenEntity verification = new EmailVerificationTokenEntity();

        verification.setToken(UUID.randomUUID().toString());
        verification.setUser(user);
        verification.setExpiresAt(LocalDateTime.now().plusHours(24));

        emailVerificationTokenRepository.save(verification);

        System.out.println("Calling EmailService...");

        emailService.sendVerificationEmail(
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                verification.getToken());

        RegisterResponse response = new RegisterResponse();
        response.setMessage("Registration successful. Please verify your email.");

        return response;
    }

    @Override
    public String verifyEmail(String token) {

        // find token
        EmailVerificationTokenEntity verification = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token."));

        // Check expiration
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification link has expired.");
        }

        // get user

        UserEntity user = verification.getUser();

        // verify user
        user.setEmailVerified(true);

        userRepository.save(user);

        // delete token after verification
        emailVerificationTokenRepository.delete(verification);

        return "Email verified successfully.";
    }

}
