package com.example.enterprise_project_management_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

import com.example.enterprise_project_management_platform.entity.EmailVerificationTokenEntity;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationTokenEntity, UUID> {

    Optional<EmailVerificationTokenEntity> findByToken(String token);
}
