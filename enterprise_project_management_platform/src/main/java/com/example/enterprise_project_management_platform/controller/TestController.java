package com.example.enterprise_project_management_platform.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
@GetMapping("/public")
public String publicApi() {
    return "Public API";
}  

@PreAuthorize("isAuthenticated()")
@GetMapping("/authenticated")
public String authenticated() {
    return "Authenticated User";
}

@PreAuthorize("hasRole('VIEWER')")
@GetMapping("/viewer")
public String viewer() {
    return "Viewer API";
}

@PreAuthorize("hasRole('DEVELOPER')")
@GetMapping("/developer")
public String developer() {
    return "Developer API";
}

@PreAuthorize("hasRole('TEAM_LEAD')")
@GetMapping("/team-lead")
public String teamLead() {
    return "Team Lead API";
}

@PreAuthorize("hasRole('PROJECT_MANAGER')")
@GetMapping("/project-manager")
public String projectManager() {
    return "Project Manager API";
}

@PreAuthorize("hasRole('ORGANIZATION_ADMIN')")
@GetMapping("/organization-admin")
public String organizationAdmin() {
    return "Organization Admin API";
}

@PreAuthorize("hasRole('SUPER_ADMIN')")
@GetMapping("/super-admin")
public String superAdmin() {
    return "Super Admin API";
}

}
