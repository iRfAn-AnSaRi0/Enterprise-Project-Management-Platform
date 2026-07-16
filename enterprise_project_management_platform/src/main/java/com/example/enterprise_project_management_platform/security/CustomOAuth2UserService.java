package com.example.enterprise_project_management_platform.security;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.enterprise_project_management_platform.entity.RoleEntity;
import com.example.enterprise_project_management_platform.entity.UserEntity;
import com.example.enterprise_project_management_platform.entity.UserRoleEntity;
import com.example.enterprise_project_management_platform.enums.Role;
import com.example.enterprise_project_management_platform.repository.RoleRepository;
import com.example.enterprise_project_management_platform.repository.UserRepository;
import com.example.enterprise_project_management_platform.repository.UserRoleRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
       System.out.println("INSIDE CustomOAuth2UserService");
        OAuth2User oauthUser = super.loadUser(userRequest);

        Map<String, Object> attributes = oauthUser.getAttributes();

        System.out.println("Google User Attributes: " + attributes);


        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        System.out.println("Email = " + email);

System.out.println(
    "User Exists = " +
    userRepository.existsByEmail(email)
);

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(
                        email,
                        name,
                        picture));

        return oauthUser;

    }

    private UserEntity createUser(String email, String name, String picture) {
             
        System.out.println("Creating user: " + email);

        UserEntity user = new UserEntity();

        String[] names = name.split(" ");

        user.setFirstName(names[0]);

        if (names.length > 1) {
            user.setLastName(names[1]);
        } else {
            user.setLastName("");
        }

        user.setEmail(email);

        user.setAvatarUrl(picture);

        // Google already verified email
        user.setEmailVerified(true);

        user.setActive(true);

        
         // temporary password
        user.setPassword("GOOGLE_AUTH_USER");


        userRepository.save(user);

         System.out.println("Saved user with ID: " + user.getId());

        RoleEntity role = roleRepository
                .findByName(Role.VIEWER.name())
                .orElseThrow(
                    () -> new RuntimeException("VIEWER role not found")
                );


        UserRoleEntity userRole = new UserRoleEntity();

        userRole.setUser(user);
        userRole.setRole(role);


        userRoleRepository.save(userRole);

 System.out.println("Assigned VIEWER role.");
        return user;


    }

}
