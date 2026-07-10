package com.example.enterprise_project_management_platform.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.enterprise_project_management_platform.entity.UserEntity;
import com.example.enterprise_project_management_platform.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{
    
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException{
          UserEntity user = userRepository.findByEmail(username)
          .orElseThrow(() -> new UsernameNotFoundException("User not found"));

          return new CustomUserDetails(user);
    }
    

}
