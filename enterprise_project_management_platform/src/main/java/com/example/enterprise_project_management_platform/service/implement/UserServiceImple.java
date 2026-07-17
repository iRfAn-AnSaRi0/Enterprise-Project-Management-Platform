package com.example.enterprise_project_management_platform.service.implement;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.enterprise_project_management_platform.dto.UserResponse;
import com.example.enterprise_project_management_platform.entity.UserEntity;
import com.example.enterprise_project_management_platform.repository.UserRepository;
import com.example.enterprise_project_management_platform.service.UserService;

import exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImple implements UserService {

   private final UserRepository userRepository;
  
    @Override
public UserResponse getCurrentUser(
        Authentication authentication
) {

    String email = authentication.getName();

    UserEntity user =
            userRepository
                    .findByEmail(email)
                    .orElseThrow(
                            () -> new UserNotFoundException(
                                    "User not found"
                            )
                    );

    return UserResponse.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .avatarUrl(user.getAvatarUrl())
            .build();
}
}
