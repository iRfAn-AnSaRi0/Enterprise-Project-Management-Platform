package com.example.enterprise_project_management_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

import com.example.enterprise_project_management_platform.entity.RoleEntity;


public interface RoleRepository extends JpaRepository<RoleEntity, UUID>{

    Optional<RoleEntity> findByName(String name);

      boolean existsByName(String name);
} 
