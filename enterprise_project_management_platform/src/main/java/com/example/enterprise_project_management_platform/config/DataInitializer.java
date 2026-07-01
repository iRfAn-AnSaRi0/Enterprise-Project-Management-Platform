package com.example.enterprise_project_management_platform.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.enterprise_project_management_platform.repository.RoleRepository;
import com.example.enterprise_project_management_platform.entity.RoleEntity;
import com.example.enterprise_project_management_platform.enums.Role;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor // to add data in role when app run first
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args){

        for (Role role : Role.values()) {
            if (!roleRepository.existsByName(role.name())) {
                RoleEntity roleEntity = new RoleEntity();
                roleEntity.setName(role.name());

                roleRepository.save(roleEntity);
            }
        }

    }
}
