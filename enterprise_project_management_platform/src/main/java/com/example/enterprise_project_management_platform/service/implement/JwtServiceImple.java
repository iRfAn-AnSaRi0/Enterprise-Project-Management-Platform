package com.example.enterprise_project_management_platform.service.implement;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.example.enterprise_project_management_platform.config.JwtConfig;
import com.example.enterprise_project_management_platform.service.JwtService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtServiceImple implements JwtService {
    private final JwtConfig jwtConfig;

    // Get the signing key from the secret
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    private String generateToken(String email, long expiration) {

        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    // Generate an access token for the given email
    @Override
    public String generateAccessToken(String email) {
        return generateToken(email, jwtConfig.getAccessTokenExpiration());
    }

    @Override
public String generateRefreshToken(String email) {
    return generateToken(email, jwtConfig.getRefreshTokenExpiration());
}

    // Extract the email from the given token
    @Override
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Check if the given token is valid
    @Override
    public boolean isValid(String token) {

        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
