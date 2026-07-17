package com.example.enterprise_project_management_platform.repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.enterprise_project_management_platform.entity.UserEntity;
import com.example.enterprise_project_management_platform.entity.UserSessionEntity;

public interface UserSessionRepository extends JpaRepository<UserSessionEntity , UUID>{

    List<UserSessionEntity> findByUser(UserEntity user);

    List<UserSessionEntity> findByUserAndActiveTrue(UserEntity user);

    Optional<UserSessionEntity> findBySessionId(String sessionId);

    
} 
