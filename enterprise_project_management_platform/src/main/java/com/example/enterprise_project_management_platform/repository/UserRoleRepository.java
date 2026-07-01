package com.example.enterprise_project_management_platform.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.enterprise_project_management_platform.entity.UserRoleEntity;


public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UUID>{

    
} 