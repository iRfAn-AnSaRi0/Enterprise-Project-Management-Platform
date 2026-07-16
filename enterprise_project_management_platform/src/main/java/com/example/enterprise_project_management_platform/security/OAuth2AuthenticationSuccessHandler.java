package com.example.enterprise_project_management_platform.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.enterprise_project_management_platform.dto.LoginResponse;
import com.example.enterprise_project_management_platform.entity.RefreshTokenEntity;
import com.example.enterprise_project_management_platform.entity.UserEntity;
import com.example.enterprise_project_management_platform.repository.RefreshTokenRepository;
import com.example.enterprise_project_management_platform.repository.UserRepository;
import com.example.enterprise_project_management_platform.service.JwtService;

import exception.UserNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// it handle all the step after login like generate both token save user and return token to frontend

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String accessToken = jwtService.generateAccessToken(user.getEmail());

        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();

        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenEntity.setRevoked(false);

        refreshTokenRepository.save(refreshTokenEntity);

        response.setContentType("application/json");

        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
                {
                    "accessToken":"%s",
                    "refreshToken":"%s",
                    "message":"Google login successful."
                }
                """.formatted(accessToken, refreshToken));

    }

}
