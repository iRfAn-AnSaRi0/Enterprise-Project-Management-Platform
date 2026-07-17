package com.example.enterprise_project_management_platform.service.implement;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.enterprise_project_management_platform.dto.ForgotPasswordRequest;
import com.example.enterprise_project_management_platform.dto.LoginRequest;
import com.example.enterprise_project_management_platform.dto.LoginResponse;
import com.example.enterprise_project_management_platform.dto.LogoutRequest;
import com.example.enterprise_project_management_platform.dto.RefreshTokenRequest;
import com.example.enterprise_project_management_platform.dto.RefreshTokenResponse;
import com.example.enterprise_project_management_platform.dto.RegisterRequest;
import com.example.enterprise_project_management_platform.dto.RegisterResponse;
import com.example.enterprise_project_management_platform.dto.ResetPasswordRequest;
import com.example.enterprise_project_management_platform.entity.EmailVerificationTokenEntity;
import com.example.enterprise_project_management_platform.entity.PasswordResetTokenEntity;
import com.example.enterprise_project_management_platform.entity.RefreshTokenEntity;
import com.example.enterprise_project_management_platform.entity.RoleEntity;
import com.example.enterprise_project_management_platform.entity.UserEntity;
import com.example.enterprise_project_management_platform.entity.UserRoleEntity;
import com.example.enterprise_project_management_platform.entity.UserSessionEntity;
import com.example.enterprise_project_management_platform.enums.Role;
import com.example.enterprise_project_management_platform.repository.EmailVerificationTokenRepository;
import com.example.enterprise_project_management_platform.repository.PasswordResetTokenRepository;
import com.example.enterprise_project_management_platform.repository.RefreshTokenRepository;
import com.example.enterprise_project_management_platform.repository.RoleRepository;
import com.example.enterprise_project_management_platform.repository.UserRepository;
import com.example.enterprise_project_management_platform.repository.UserRoleRepository;
import com.example.enterprise_project_management_platform.repository.UserSessionRepository;
import com.example.enterprise_project_management_platform.service.AuthService;
import com.example.enterprise_project_management_platform.service.EmailService;
import com.example.enterprise_project_management_platform.service.JwtService;

import exception.AlreadyLoggedOutException;
import exception.InvalidCredentialsException;
import exception.InvalidTokenException;
import exception.TokenAlreadyUsedException;
import exception.TokenExpiredException;
import exception.UserAlreadyExistsException;
import exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserSessionRepository userSessionRepository;

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail((request.getEmail()))) { // check for existing user
            throw new UserAlreadyExistsException("Email already exists");
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
                .orElseThrow(() -> new InvalidTokenException("Invalid verification token."));

        // Check expiration
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Verification link has expired.");
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

    @Override
    public LoginResponse login(LoginRequest request, HttpServletRequest httpServletRequest) {

        // find user by email
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password."));

        // check if email is verified
        if (!user.isEmailVerified()) {
            throw new RuntimeException("Please verify your email first.");
        }

        // check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password.");
        }

        // generate access token
        String accessToken = jwtService.generateAccessToken(user.getEmail());

        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();

        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenEntity.setRevoked(false);

        refreshTokenRepository.save(refreshTokenEntity);

        UserSessionEntity session = new UserSessionEntity();
        session.setUser(user);
        session.setSessionId(UUID.randomUUID().toString());

        session.setDeviceName("Chrome");
        session.setIpAddress(
                httpServletRequest.getRemoteAddr());

        session.setUserAgent(
                httpServletRequest.getHeader("User-Agent"));

        session.setLoginTime(LocalDateTime.now());
        session.setLastActive(LocalDateTime.now());
        session.setExpiresAt(
                LocalDateTime.now().plusDays(7));

        session.setActive(true);

        userSessionRepository.save(session);

        // prepare response
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setMessage("Login successful.");

        return response;

    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token."));

        if (refreshTokenEntity.isRevoked()) {
            throw new InvalidCredentialsException("Refresh token has been revoked.");
        }

        if (refreshTokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Refresh token has expired.");
        }

        if (!jwtService.isValid(request.getRefreshToken())) {
            throw new InvalidTokenException("Refresh token is not valid.");
        }

        String email = jwtService.extractEmail(request.getRefreshToken());

        String newAccessToken = jwtService.generateAccessToken(email);

        return new RefreshTokenResponse(newAccessToken);
    }

    @Override
    public void logout(LogoutRequest request) {

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token."));

        if (refreshTokenEntity.isRevoked()) {
            throw new AlreadyLoggedOutException("User already logged out.");
        }

        refreshTokenEntity.setRevoked(true);

        refreshTokenRepository.save(refreshTokenEntity);

    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();

        PasswordResetTokenEntity resetToken = new PasswordResetTokenEntity();

        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        resetToken.setUsed(false);

        passwordResetTokenRepository.save(resetToken);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetTokenEntity resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));

        if (resetToken.isUsed()) {
            throw new TokenAlreadyUsedException("This reset link has already been used.");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Reset link has expired.");
        }

        UserEntity user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

}
