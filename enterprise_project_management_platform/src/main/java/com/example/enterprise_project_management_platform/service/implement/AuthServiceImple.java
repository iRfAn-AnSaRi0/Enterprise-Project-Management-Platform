package com.example.enterprise_project_management_platform.service.implement;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.enterprise_project_management_platform.dto.RegisterRequest;
import com.example.enterprise_project_management_platform.dto.RegisterResponse;
import com.example.enterprise_project_management_platform.entity.RoleEntity;
import com.example.enterprise_project_management_platform.entity.UserEntity;
import com.example.enterprise_project_management_platform.entity.UserRoleEntity;
import com.example.enterprise_project_management_platform.enums.Role;
import com.example.enterprise_project_management_platform.repository.EmailVerificationTokenRepository;
import com.example.enterprise_project_management_platform.repository.RoleRepository;
import com.example.enterprise_project_management_platform.repository.UserRepository;
import com.example.enterprise_project_management_platform.repository.UserRoleRepository;
import com.example.enterprise_project_management_platform.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImple implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail((request.getEmail()))) {  // check for existing user
            throw new RuntimeException("Email already exists");
        }

        String encodePassword = passwordEncoder.encode(request.getPassword()); // hash the password

        UserEntity user = new UserEntity(); // now save the user in DB
        UserRoleEntity userRole = new UserRoleEntity(); // save the user role and user 
        RoleEntity role = roleRepository.findByName(Role.VIEWER.name()).orElseThrow(() -> new RuntimeException("Role not found"));

        // save user details
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(encodePassword);

        //save user role in userRole table
        userRole.setUser(user);
        userRole.setRole(role);



        userRepository.save(user);
        userRoleRepository.save(userRole);

        return null;
    }

}
